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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
@SuppressWarnings("unused")
public class FilmWithGenresDataMapper implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));

                java.sql.Date sqlDate = rs.getDate("release_date");
                film.setReleaseDate(sqlDate != null ? sqlDate.toLocalDate() : null);

                film.setDuration(rs.getInt("duration"));

                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getInt("mpa_id"));
                mpaRating.setName(rs.getString("mpa_name"));
                film.setMpa(mpaRating.getId() == 0 ? null : mpaRating);

                film.setGenres(new HashSet<>());
                filmMap.put(filmId, film);
            }

            String aggregatedGenreIds = null;
            String aggregatedGenreNames = null;
            try {
                aggregatedGenreIds = rs.getString("genre_ids");
                aggregatedGenreNames = rs.getString("genre_names");
            } catch (SQLException e) {
                // Если столбцы отсутствуют, агрегированные данные не возвращаются – перейдем к старой логике
            }

            if (aggregatedGenreIds != null && aggregatedGenreNames != null) {
                String[] ids = aggregatedGenreIds.split(",");
                String[] names = aggregatedGenreNames.split(",");
                if (ids.length == names.length) {
                    for (int i = 0; i < ids.length; i++) {
                        try {
                            int genreId = Integer.parseInt(ids[i].trim());
                            String genreName = names[i].trim();
                            film.getGenres().add(new Genre(genreId, genreName));
                        } catch (NumberFormatException e) {
                            // Пропускаем некорректные значения
                        }
                    }
                }
            } else {
                int genreId = rs.getInt("genre_id");
                if (genreId != 0) {
                    String genreName = rs.getString("genre_name");
                    if (genreName != null) {
                        film.getGenres().add(new Genre(genreId, genreName));
                    }
                }
            }
        }

        return new ArrayList<>(filmMap.values());
    }
}
