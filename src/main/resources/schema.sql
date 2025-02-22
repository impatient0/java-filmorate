CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    duration INTEGER,
    mpa_rating VARCHAR(10) -- Using VARCHAR for MPA_RATING as H2 ENUM might need more setup
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id SMALLINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genres (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id BIGINT NOT NULL,
    genre_id SMALLINT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE,
    UNIQUE (film_id, genre_id) -- Composite Unique Key to prevent duplicate film-genre entries
);

CREATE TABLE IF NOT EXISTS friendships (
    friendship_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UNCONFIRMED', -- Using VARCHAR for STATUS for simplicity in H2
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    like_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    liked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    UNIQUE (user_id, film_id) -- Composite Unique Key to prevent duplicate likes from same user to same film
);