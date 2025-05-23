

-- db-1.0.sql
-- liquibase formatted sql
-- changeset marvin:1

CREATE TABLE actor (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE video_streaming (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    video_url VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    synopsis VARCHAR(255) NOT NULL,
    director VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    duration INTEGER NOT NULL,
    year_of_release INTEGER NOT NULL,
    view_count INTEGER NOT NULL,
    impression_count INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    main_actor_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (main_actor_id) REFERENCES actor(id)
);

CREATE TABLE video_streaming_cast (
    video_streaming_id BIGINT NOT NULL,
    cast_id BIGINT NOT NULL,
    PRIMARY KEY (video_streaming_id, cast_id),
    FOREIGN KEY (video_streaming_id) REFERENCES video_streaming(id),
    FOREIGN KEY (cast_id) REFERENCES actor(id)
);


-- Rollback: Removing tables
--DROP TABLE actor;
--DROP TABLE video_streaming;
--DROP TABLE video_streaming_cast;