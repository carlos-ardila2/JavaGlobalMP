CREATE SCHEMA IF NOT EXISTS messages_db;
SET SCHEMA messages_db;
CREATE TABLE messages (id integer not null, timestamp datetime not null, recipient VARCHAR(100) NOT NULL, message VARCHAR(5000) NOT NULL);