package com.epam.jmp.spring.task3.services;

import com.epam.jmp.spring.task3.models.Book;
import com.epam.jmp.spring.task3.models.Chapter;
import com.epam.jmp.spring.task3.models.Review;
import com.epam.jmp.spring.task3.repos.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles(value = "test")
@SpringBootTest
@Import(BookServiceUnitTest.BookRepositoryImplTestConfiguration.class)
public class BookServiceUnitTest {

    @TestConfiguration
    static class BookRepositoryImplTestConfiguration {

        private final BookRepository bookRepository = mock(BookRepository.class);

        private final Book book1 = new Book(1L, "Test Book 1", LocalDate.parse("2018-03-05"), null, 1L,
                Set.of(new Chapter(1L, "Test Chapter 1", "Test Chapter 1 Content", null, Set.of(
                new Review(1L, "Test Review 1", null, 1L, null, 1L)))));

        private ArgumentMatcher<LocalDate> matchPublishedDate() {
            return d -> d != null &&  book1.getPublishedDate().getYear() > d.getYear();
        }

        @Primary
        @Bean("mockBookRepository")
        public BookRepository getBookRepository() {

            when(bookRepository.findById(1L)).thenReturn(java.util.Optional.of(book1));

            var allBooks = List.of(book1, new Book(2L, "Test Book 2",
                    LocalDate.parse("2012-11-16"), null, 2L, new HashSet<>()));

            when(bookRepository.findAll()).thenReturn(allBooks);
            when(bookRepository.findAllByPublishedDateAfter(argThat(matchPublishedDate()))).thenReturn(List.of(book1));

            return bookRepository;
        }
    }

    @MockBean
    private JwtEncoder jwtEncoder;

    @Autowired
    private BookService bookService;

    @Test
    public void whenFindBookWithMostReviews_thenReturnBook() {
        assertTrue(bookService.findBookWithMostReviews().getReviewCount() > 0);
    }

    @Test
    public void whenFindBooksPublishedAfterYear_thenReturnBooks() {
        bookService.findBooksPublishedAfter(2016).forEach(b -> assertTrue(b.getPublishedDate().getYear() >= 2016));
    }

    @Test
    public void whenFindBooksPublishedBeforeYear_thenReturnBooks() {
        assertTrue(bookService.findBooksPublishedAfter(2020).isEmpty());
    }
}
