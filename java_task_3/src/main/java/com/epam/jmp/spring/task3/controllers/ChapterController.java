package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.models.Chapter;
import com.epam.jmp.spring.task3.repos.BookRepository;
import com.epam.jmp.spring.task3.repos.ChapterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterRepository chapterRepository;
    private final BookRepository bookRepository;

    public ChapterController(ChapterRepository chapterRepository, BookRepository bookRepository) {
        this.chapterRepository = chapterRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/add/{bookId}")
    public Chapter addChapterToBook(@PathVariable Long bookId, @RequestBody Chapter chapter) {
        if (bookId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book ID is required");
        }

        chapter.setBook(this.bookRepository.getReferenceById(bookId));
        return chapterRepository.save(chapter);
    }

    @GetMapping
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    @GetMapping("/{id}")
    public Chapter getChapterById(@PathVariable Long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Chapter updateChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        var chapterToUpdate = chapterRepository.findById(id).orElseThrow();
        chapterToUpdate.setTitle(chapter.getTitle());
        chapterToUpdate.setContent(chapter.getContent());
        return chapterRepository.save(chapterToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteChapter(@PathVariable Long id) {
        chapterRepository.deleteById(id);
    }
}
