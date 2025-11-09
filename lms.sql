CREATE DATABASE librarydb;
USE librarydb;

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    author VARCHAR(100),
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE issued_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    issued_to VARCHAR(100),
    issue_date DATE,
    return_date DATE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);
