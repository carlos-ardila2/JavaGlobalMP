package com.epam.jmp.spring.task3.actuator;

import com.epam.jmp.spring.task3.repos.AuthorRepository;
import com.epam.jmp.spring.task3.repos.BookRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("bookstore")
public class PublishedBooksHealthCheck implements HealthIndicator {

    final AuthorRepository authorRepository;
    final BookRepository bookRepository;

    public PublishedBooksHealthCheck(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Health health() {
        int errorCode = 0;
        String errorMessage = "No errors";

        int authorsCount = authorRepository.findAll().size();
        int booksCount = bookRepository.findAll().size();

        if (authorRepository.findAll().isEmpty()) {
            errorCode = -2;
            errorMessage = "No authors found";
        } else if (bookRepository.findAll().isEmpty()) {
            errorCode = -1;
            errorMessage = "No books found";
        }

        if (errorCode != 0) {
            return Health.down().withDetail(errorMessage, errorCode).build();
        }
        return Health.up().withDetail("authors", authorsCount).withDetail("publishedBooks", booksCount).build();
    }
}
