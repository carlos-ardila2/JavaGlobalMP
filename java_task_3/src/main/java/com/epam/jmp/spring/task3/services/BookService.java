package com.epam.jmp.spring.task3.services;

import com.epam.jmp.spring.task3.models.Book;
import com.epam.jmp.spring.task3.repos.BookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findBookWithMostReviews() {
        return bookRepository.findAll().stream().max(
                Comparator.comparingInt(Book::getReviewCount)).orElse(null);
    }

    public List<Book> findBooksPublishedAfter(int year) {
        return bookRepository.findAllByPublishedDateAfter(LocalDate.ofYearDay(year, 1));
    }
}
