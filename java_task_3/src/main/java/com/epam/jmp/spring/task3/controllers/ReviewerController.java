package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.models.Reviewer;
import com.epam.jmp.spring.task3.repos.ReviewerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewers")
public class ReviewerController {

    private final ReviewerRepository reviewerRepository;

    public ReviewerController(ReviewerRepository reviewerRepository) {
        this.reviewerRepository = reviewerRepository;
    }

    @PostMapping
    public Reviewer createReviewer(@RequestBody Reviewer reviewer) {
        return reviewerRepository.save(reviewer);
    }

    @GetMapping
    public List<Reviewer> getAllReviewers() {
        return reviewerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Reviewer getReviewerById(@PathVariable Long id) {
        return reviewerRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Reviewer updateReviewer(@PathVariable Long id, @RequestBody Reviewer reviewer) {
        var reviewerToUpdate = reviewerRepository.findById(id).orElseThrow();
        reviewerToUpdate.setName(reviewer.getName());
        return reviewerRepository.save(reviewerToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewer(@PathVariable Long id) {
        reviewerRepository.deleteById(id);
    }
}
