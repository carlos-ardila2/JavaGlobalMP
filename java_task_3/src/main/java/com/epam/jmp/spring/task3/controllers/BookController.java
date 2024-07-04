package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.models.Book;
import com.epam.jmp.spring.task3.repos.AuthorRepository;
import com.epam.jmp.spring.task3.repos.BookRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        if (book.getAuthorId() != null) {
            book.setAuthor(authorRepository.getReferenceById(book.getAuthorId()));
        }
        return bookRepository.save(book);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        var bookToUpdate = bookRepository.findById(id).orElseThrow();
        bookToUpdate.setTitle(book.getTitle());
        if (book.getAuthorId() != null && !book.getAuthorId().equals(bookToUpdate.getAuthorId())) {
            bookToUpdate.setAuthor(authorRepository.getReferenceById(book.getAuthorId()));
        }
        return bookRepository.save(bookToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
    }
}
