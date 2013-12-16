--liquibase formatted sql

--changeset hightide-sample:1 failOnError:false
CREATE TABLE posts2 (
    id BIGINT PRIMARY KEY NOT NULL,
    body VARCHAR(255),
    timestamp DATETIME,
    title VARCHAR(255)
);
--rollback DROP TABLE posts2;

--changeset gpan:2 failOnError:false
CREATE TABLE posts6 (
    id BIGINT PRIMARY KEY NOT NULL,
    body VARCHAR(255),
    timestamp DATETIME,
    title VARCHAR(255)
);
--rollback DROP TABLE posts6;

--changeset gpan:3 failOnError:false
CREATE TABLE posts3 (
    id BIGINT PRIMARY KEY NOT NULL,
    body VARCHAR(255),
    timestamp DATETIME,
    title VARCHAR(255)
);
--rollback DROP TABLE posts3;