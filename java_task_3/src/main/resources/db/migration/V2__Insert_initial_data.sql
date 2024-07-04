-- Insert into Author
INSERT INTO Author (name) VALUES ('Freddie Mac');
INSERT INTO Author (name) VALUES ('Fanny Mae');

-- Insert into Book
INSERT INTO Book (title, published_date, author_id) VALUES ('Meditations About Stuff', '2020-09-22', 1);
INSERT INTO Book (title, published_date, author_id) VALUES ('Life Lessons', '2017-12-25', 2);

-- Insert into Chapter
INSERT INTO Chapter (title, content, book_id) VALUES ('About Life', 'Life is like a box of chocolates, you never know what are you gonna get...', 1);
INSERT INTO Chapter (title, content, book_id) VALUES ('About Love', 'Love is to never have to say you are sorry.', 1);
INSERT INTO Chapter (title, content, book_id) VALUES ('Life is Good', 'Every day is a gift.', 2);

-- Insert into Reviewer
INSERT INTO Reviewer (name) VALUES ('Jack the Critic');
INSERT INTO Reviewer (name) VALUES ('Vicky the Reviewer');

-- Insert into Review
INSERT INTO Review (content, book_chapter_id, reviewer_id) VALUES ('Not bad, but it needs further elaboration.', 1, 1);
INSERT INTO Review (content, book_chapter_id, reviewer_id) VALUES ('Could use some factual data.', 2, 1);
INSERT INTO Review (content, book_chapter_id, reviewer_id) VALUES ('So insightful!', 3, 2);