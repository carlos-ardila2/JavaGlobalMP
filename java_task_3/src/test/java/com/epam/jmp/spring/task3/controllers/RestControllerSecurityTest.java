package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.configuration.SecurityConfiguration;
import com.epam.jmp.spring.task3.dtos.OAuth2TokenResponse;
import com.epam.jmp.spring.task3.models.Author;
import com.epam.jmp.spring.task3.repos.AuthorRepository;
import com.epam.jmp.spring.task3.repos.BookRepository;
import com.epam.jmp.spring.task3.repos.ReviewerRepository;
import com.epam.jmp.spring.task3.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "stg")
@WebMvcTest({AuthorController.class, WebController.class})
@Import(SecurityConfiguration.class)
public class RestControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private ReviewerRepository reviewerRepository;

    @MockBean
    private BookService bookService;

    @Test
    void tokenWhenAuthenticatedThenAuthor() throws Exception {

        Author author = new Author(1L, "Jim", new HashSet<>());
        given(authorRepository.findById(anyLong())).willReturn(Optional.of(author));

        MvcResult result = this.mvc.perform(post("/oauth/token")
                        .with(httpBasic("admin", "pass")))
                .andExpect(status().isOk())
                .andReturn();

        String token = mapper.readValue(result.getResponse().getContentAsString(), OAuth2TokenResponse.class).getAccess_token();

        this.mvc.perform(get("/api/authors/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(author.getId().intValue())));
    }

    @Test
    void tokenWhenBadCredentialsThen401() throws Exception {
        this.mvc.perform(post("/oauth/token")
                        .with(httpBasic("user", "bad")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorWhenUnauthenticatedThen401() throws Exception {
        this.mvc.perform(get("/api/authors/1"))
                .andExpect(status().isUnauthorized());
    }
}
