# java-filmorate

## DB schema:

(prettier version
available [here](https://drive.google.com/file/d/180JIfZNXEnq3Se4-3l66gs4mYytXpNV-/view?usp=sharing))

```mermaid
erDiagram
    users {
        BIGINT user_id PK
        VARCHAR email
        VARCHAR login
        VARCHAR name
        DATE birthday
    }

    films {
        BIGINT film_id PK
        VARCHAR name
        TEXT description
        DATE release_date
        INTEGER duration
        SMALLINT mpa_rating_id FK
    }

    mpa_ratings {
        SMALLINT mpa_id PK
        VARCHAR name
    }

    genres {
        SMALLINT genre_id PK
        VARCHAR name
    }

    film_genres {
        BIGINT film_id FK
        SMALLINT genre_id FK
    }

    friendships {
        BIGINT user_id FK
        BIGINT friend_id FK
        VARCHAR status
        TIMESTAMP created_at
    }

    likes {
        BIGINT user_id FK
        BIGINT film_id FK
        TIMESTAMP created_at
    }

    users ||--o{ friendships : "friendships (user, friend)"
    users ||--o{ likes : "likes"
    films ||--o{ likes : "likes"
    films ||--o{ film_genres : "film_genres"
    genres ||--o{ film_genres : "film_genres"
    films }o--|| mpa_ratings : "mpa_rating"
```
### Example queries
- Get all films (without genres):
```sql
SELECT
    *
FROM
    films;
```
- Get user by ID:
```sql
SELECT
    *
FROM
    users
WHERE
    user_id = ?; -- Parameter: User ID
```

- Select film by ID with genres and MPA rating (data comes back denormalized and is later processed
  by extractor):
```sql
SELECT
    f.film_id,
    f.name AS film_name,
    f.description,
    f.release_date,
    f.duration,
    m.mpa_id,
    m.name AS mpa_name,
    g.genre_id,
    g.name AS genre_name
FROM
    films f
LEFT JOIN
    mpa_ratings m ON f.mpa_rating_id = m.mpa_id
LEFT JOIN
    film_genres fg ON f.film_id = fg.film_id
LEFT JOIN
    genres g ON fg.genre_id = g.genre_id
WHERE
    f.film_id = ?; -- Parameter: Film ID
```

- Get top most liked films:
```sql
WITH film_likes AS (
    SELECT
        film_id,
        COUNT(film_id) AS likes_count
    FROM
        likes
    GROUP BY
        film_id
)
SELECT
    f.film_id,
    f.name AS film_name,
    f.description,
    f.release_date,
    f.duration,
    m.mpa_id,
    m.name AS mpa_name,
    g.genre_id,
    g.name AS genre_name,
    film_likes.likes_count
FROM
    films f
JOIN
    mpa_ratings m ON f.mpa_rating_id = m.mpa_id
LEFT JOIN
    film_genres fg ON f.film_id = fg.film_id
LEFT JOIN
    genres g ON fg.genre_id = g.genre_id
JOIN
    film_likes ON f.film_id = film_likes.film_id
ORDER BY
    film_likes.likes_count DESC,
    f.film_id,
    g.genre_id
LIMIT
    ?; -- Parameter: number of films to fetch
```
