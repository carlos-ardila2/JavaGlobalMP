CREATE TABLE Author (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

CREATE TABLE Book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    published_date DATE,
    author_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES Author(id)
);

CREATE TABLE Chapter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    content VARCHAR(255),
    book_id BIGINT,
    FOREIGN KEY (book_id) REFERENCES Book(id)
);

CREATE TABLE Reviewer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

CREATE TABLE Review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT,
    book_chapter_id BIGINT,
    reviewer_id BIGINT,
    FOREIGN KEY (book_chapter_id) REFERENCES Chapter(id),
    FOREIGN KEY (reviewer_id) REFERENCES Reviewer(id)
);