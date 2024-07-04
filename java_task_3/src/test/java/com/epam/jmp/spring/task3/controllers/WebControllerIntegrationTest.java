package com.epam.jmp.spring.task3.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "stg")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class WebControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "spring")
    @Test
    public void givenAuthRequestOnSecureService_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/reviewers").contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("reviewers"))
                .andExpect(model().attributeExists("reviewerList"))
                .andExpect(content().string(containsString("Reviewers")))
        ;
    }

    @Test
    public void givenUnauthenticated_whenCallService_thenThrowsException() throws Exception {
        mvc.perform(get("/api/authors/1")).andExpect(status().is4xxClientError());
    }
}
