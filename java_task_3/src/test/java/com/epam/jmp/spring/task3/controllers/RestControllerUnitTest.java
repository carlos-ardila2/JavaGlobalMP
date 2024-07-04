package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.configuration.NoSecurityConfiguration;
import com.epam.jmp.spring.task3.models.Author;
import com.epam.jmp.spring.task3.models.Book;
import com.epam.jmp.spring.task3.models.Chapter;
import com.epam.jmp.spring.task3.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@WebMvcTest(AuthorController.class)
@Import(NoSecurityConfiguration.class)
public class RestControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorRepository authorRepository;

    @Test
    public void givenAuthors_whenGetAuthors_thenReturnJsonArray()
            throws Exception {

        Author theAuthor = new Author(1L, "Moses", new HashSet<>());
        Book theBook = new Book(1L, "The Good Book", LocalDate.parse("2020-11-16"), theAuthor, theAuthor.getId(), new HashSet<>());
        Chapter theChapter = new Chapter(1L, "Genesis", "In the beginning...", theBook, new HashSet<>());

        theBook.getChapters().add(theChapter);
        theAuthor.getBooks().add(theBook);

        List<Author> allAuthors = List.of(theAuthor);

        given(authorRepository.findAll()).willReturn(allAuthors);

        mvc.perform(get("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(theAuthor.getName())))
                .andExpect(jsonPath("$[0].booksPresent", is(true)))
                .andExpect(jsonPath("$[0].books", hasSize(1)))
                .andExpect(jsonPath("$[0].books[0].title", is(theBook.getTitle())))
                .andExpect(jsonPath("$[0].books[0].publishedDate", is(theBook.getPublishedDate().toString())))
                .andExpect(jsonPath("$[0].books[0].authorId", is(theAuthor.getId().intValue())))
                .andExpect(jsonPath("$[0].books[0].chaptersPresent", is(true)))
                .andExpect(jsonPath("$[0].books[0].chaptersTitles", is("Genesis")))
                .andExpect(jsonPath("$[0].books[0].authorName", is("Moses")))
                .andExpect(jsonPath("$[0].books[0].chapters", hasSize(1)))
                .andExpect(jsonPath("$[0].books[0].chapters[0].title", is(theChapter.getTitle())))
                .andExpect(jsonPath("$[0].books[0].chapters[0].content", is(theChapter.getContent())))
                .andExpect(jsonPath("$[0].books[0].chapters[0].reviewsPresent", is(false)));
    }
}
