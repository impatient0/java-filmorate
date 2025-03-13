package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
@SuppressWarnings("unused")
public class FilmWithAvgRatingDataMapper implements ResultSetExtractor<List<FilmWithRating>> {

    @Override
    public List<FilmWithRating> extractData(ResultSet rs) throws SQLException {
        List<FilmWithRating> filmWithRatings = new ArrayList<>();

        Map<Long, Film> filmMap = new LinkedHashMap<>(); // To reuse film objects for genres
        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getInt("mpa_id"));
                mpaRating.setName(rs.getString("mpa_name"));
                if (mpaRating.getId() == 0) {
                    film.setMpa(null);
                } else {
                    film.setMpa(mpaRating);
                }
                film.setGenres(
                    new HashSet<>()); // Use LinkedHashMap to maintain genre order if needed
                filmMap.put(filmId, film);

                double avgRating = rs.getDouble(
                    "avg_rating"); // Handle cases where avg_rating might be NULL (no ratings yet)
                if (rs.wasNull()) {
                    filmWithRatings.add(new FilmWithRating(film, 0.0));
                } else {
                    filmWithRatings.add(new FilmWithRating(film, avgRating));
                }
            }

            int genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre); // Using put with id as key for easier access if needed
            }
        }
        return filmWithRatings;
    }
}
