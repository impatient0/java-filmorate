package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

@Slf4j
@Repository
public class DbReviewStorage extends DbBaseStorage<Review> implements ReviewStorage {

    private static final String INSERT_REVIEW =
            "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, 0)";
    private static final String UPDATE_REVIEW =
            "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE review_id = ?";
    private static final String GET_REVIEW_BY_ID =
            "SELECT * FROM reviews WHERE review_id = ?";
    private static final String GET_REVIEWS_BY_FILM =
            "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC, review_id LIMIT ?";
    private static final String GET_ALL_REVIEWS =
            "SELECT * FROM reviews ORDER BY useful DESC, review_id LIMIT ?";

    private static final String INSERT_REVIEW_LIKE =
            "INSERT INTO review_likes (review_id, user_id, like_value) VALUES (?, ?, 1)";
    private static final String INSERT_REVIEW_DISLIKE =
            "INSERT INTO review_likes (review_id, user_id, like_value) VALUES (?, ?, -1)";
    private static final String DELETE_REVIEW_LIKE =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_REVIEW_USEFUL =
            "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
    private static final String CHECK_LIKE_DISLIKE =
            "SELECT like_value FROM review_likes WHERE review_id = ? AND user_id = ?";

    private static final RowMapper<Review> reviewRowMapper = (rs, rowNum) -> {
        Review r = new Review();
        r.setReviewId(rs.getLong("review_id"));
        r.setContent(rs.getString("content"));
        r.setPositive(rs.getBoolean("is_positive"));
        r.setUserId(rs.getLong("user_id"));
        r.setFilmId(rs.getLong("film_id"));
        r.setUseful(rs.getInt("useful"));
        return r;
    };

    public DbReviewStorage(JdbcTemplate jdbc) {
        super(jdbc, reviewRowMapper);
    }

    @Override
    public Review addReview(Review review) {
        long id = insert(INSERT_REVIEW,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        update(UPDATE_REVIEW, review.getContent(), review.isPositive(), review.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(long reviewId) {
        delete(DELETE_REVIEW, reviewId);
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        try {
            Review review = jdbc.queryForObject(GET_REVIEW_BY_ID, reviewRowMapper, reviewId);
            return Optional.ofNullable(review);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> getReviews(Long filmId, int count) {
        if (filmId != null) {
            return jdbc.query(GET_REVIEWS_BY_FILM, reviewRowMapper, filmId, count);
        } else {
            return jdbc.query(GET_ALL_REVIEWS, reviewRowMapper, count);
        }
    }

    @Override
    public void addLike(long reviewId, long userId) {
        SqlRowSet rowSet = jdbc.queryForRowSet(CHECK_LIKE_DISLIKE, reviewId, userId);
        if (rowSet.next()) {
            int likeValue = rowSet.getInt("like_value");
            if (likeValue == 1) {
                return; // Лайк уже установлен
            } else if (likeValue == -1) {
                removeDislike(reviewId, userId); // Удаляем существующий дизлайк
            }
        }
        jdbc.update(INSERT_REVIEW_LIKE, reviewId, userId);
        jdbc.update(UPDATE_REVIEW_USEFUL, 1, reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        SqlRowSet rowSet = jdbc.queryForRowSet(CHECK_LIKE_DISLIKE, reviewId, userId);
        if (rowSet.next()) {
            int likeValue = rowSet.getInt("like_value");
            if (likeValue == -1) {
                return; // Дизлайк уже установлен
            } else if (likeValue == 1) {
                removeLike(reviewId, userId); // Удаляем лайк перед добавлением дизлайка
            }
        }
        jdbc.update(INSERT_REVIEW_DISLIKE, reviewId, userId);
        jdbc.update(UPDATE_REVIEW_USEFUL, -1, reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        jdbc.update(DELETE_REVIEW_LIKE, reviewId, userId);
        jdbc.update(UPDATE_REVIEW_USEFUL, -1, reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        jdbc.update(DELETE_REVIEW_LIKE, reviewId, userId);
        jdbc.update(UPDATE_REVIEW_USEFUL, 1, reviewId);
    }

    @Override
    public boolean hasDislike(long reviewId, long userId) {
        SqlRowSet rowSet = jdbc.queryForRowSet(CHECK_LIKE_DISLIKE, reviewId, userId);
        if (rowSet.next()) {
            return rowSet.getInt("like_value") == -1;
        }
        return false;
    }
}
