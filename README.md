# java-filmorate

## DB schema:
(prettier verion available [here](https://drive.google.com/file/d/180JIfZNXEnq3Se4-3l66gs4mYytXpNV-/view?usp=sharing))

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
        VARCHAR mpa_rating
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

    users ||--o{ friendships : "friendships (user_id)"
    users ||--o{ friendships : "friendships (friend_id)"
    users ||--o{ likes : "likes"
    films ||--o{ likes : "likes"
    films ||--o{ film_genres : "film_genres"
    genres ||--o{ film_genres : "film_genres"
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
- Get genres for film:
```sql
SELECT
    g.genre_id,
    g.name
FROM
    film_genres fg
JOIN
    genres g ON fg.genre_id = g.genre_id
WHERE
    fg.film_id = ?; -- Parameter: Film ID
```
- Get top 10 most liked films:
```sql
SELECT
    f.film_id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.mpa_rating,
    COUNT(l.like_id) AS like_count
FROM
    films f
LEFT JOIN
    likes l ON f.film_id = l.film_id
GROUP BY
    f.film_id
ORDER BY
    like_count DESC
LIMIT 10;
```
