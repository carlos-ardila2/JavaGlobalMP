package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.dtos.OAuth2TokenResponse;
import com.epam.jmp.spring.task3.repos.AuthorRepository;
import com.epam.jmp.spring.task3.repos.BookRepository;
import com.epam.jmp.spring.task3.repos.ReviewerRepository;
import com.epam.jmp.spring.task3.services.BookService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.stream.Collectors;

@Controller
public class WebController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ReviewerRepository reviewerRepository;
    private final BookService bookService;

    private final JwtEncoder encoder;

    public WebController(BookRepository bookRepository, AuthorRepository authorRepository,
                         ReviewerRepository reviewerRepository, BookService bookService, JwtEncoder encoder) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.reviewerRepository = reviewerRepository;
        this.bookService = bookService;
        this.encoder = encoder;
    }

    @GetMapping(value="/books")
    public String books(final Model model) {
        model.addAttribute("bookList", bookRepository.findAll());
        model.addAttribute("authorList", authorRepository.findAll());
        model.addAttribute("reviewerList", reviewerRepository.findAll());
        model.addAttribute("bookWithMostReviews", bookService.findBookWithMostReviews());
        return "books";
    }

    @GetMapping(value="/authors")
    public String authors(final Model model) {
        model.addAttribute("authorList", authorRepository.findAll());
        return "authors";
    }

    @GetMapping(value="/csrf", produces = "application/json")
    public @ResponseBody CsrfToken csrf(@RequestBody CsrfToken csrfToken) {
        return csrfToken;
    }

    @GetMapping(value="/reviewers")
    public String reviewers(final Model model) {
        model.addAttribute("reviewerList", reviewerRepository.findAll());
        return "reviewers";
    }

    @PostMapping("/oauth/token")
    public @ResponseBody OAuth2TokenResponse token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return new OAuth2TokenResponse(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(),
                "Bearer", expiry, scope);
    }
}
