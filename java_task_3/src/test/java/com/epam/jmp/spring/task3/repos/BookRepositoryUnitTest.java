package com.epam.jmp.spring.task3.repos;

import com.epam.jmp.spring.task3.models.Author;
import com.epam.jmp.spring.task3.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles(value = "test")
@DataJpaTest
public class BookRepositoryUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private static final String AUTHOR_NAME = "Test Author 2";
    private static final String BOOK_TITLE = "Test Book 2";

    @BeforeEach
    public void setUp() {
        Author author1 = new Author();
        author1.setName("Test Author 1");

        Book book1 = new Book();
        book1.setTitle("Test Book 1");
        book1.setPublishedDate(LocalDate.parse("2018-03-05"));
        book1.setAuthor(author1);
        author1.setBooks(Set.of(book1));

        Author author2 = new Author();
        author2.setName(AUTHOR_NAME);

        Book book2 = new Book();
        book2.setTitle(BOOK_TITLE);
        book2.setPublishedDate(LocalDate.parse("2020-11-16"));
        book2.setAuthor(author2);
        author2.setBooks(Set.of(book2));

        entityManager.persist(author1);
        entityManager.persist(author2);
        entityManager.flush();
    }

    @Test
    public void whenFindAll_theReturnAuthors() {
        assertThat(authorRepository.count()).isGreaterThanOrEqualTo(2L);
    }

    @Test
    public void whenFindByPublishedAfter_thenReturnBook() {
        var found = bookRepository.findAllByPublishedDateAfter(LocalDate.ofYearDay(2020, 1));

        assertThat(found.size()).isEqualTo(1);
        assertThat(found.getFirst().getTitle()).isEqualTo(BOOK_TITLE);
        assertThat(found.getFirst().getAuthor().getName()).isEqualTo(AUTHOR_NAME);
    }
}
