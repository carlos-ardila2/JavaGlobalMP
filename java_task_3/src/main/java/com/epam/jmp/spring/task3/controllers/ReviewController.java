package com.epam.jmp.spring.task3.controllers;

import com.epam.jmp.spring.task3.models.Review;
import com.epam.jmp.spring.task3.repos.ChapterRepository;
import com.epam.jmp.spring.task3.repos.ReviewRepository;
import com.epam.jmp.spring.task3.repos.ReviewerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewerRepository reviewerRepository;
    private final ChapterRepository chapterRepository;

    private final Counter counter;

    public ReviewController(ReviewRepository reviewRepository, ReviewerRepository reviewerRepository,
                            ChapterRepository chapterRepository, MeterRegistry meterRegistry) {
        this.reviewRepository = reviewRepository;
        this.reviewerRepository = reviewerRepository;
        this.chapterRepository = chapterRepository;

        this.counter = Counter.builder("bookstore.reviews.added")
                .description("Total number of reviews added")
                .register(meterRegistry);
    }

    @PostMapping("/add/{chapterId}")
    public Review addReviewToChapter(@PathVariable Long chapterId, @RequestBody Review review) {
        if (chapterId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter ID is required");
        }

        review.setBookChapter(this.chapterRepository.getReferenceById(chapterId));

        if (review.getReviewerId() != null) {
            review.setReviewer(reviewerRepository.getReferenceById(review.getReviewerId()));
        }

        try {
            var savedReview = reviewRepository.save(review);
            counter.increment();
            return savedReview;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save review");
        }
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review review) {
        var reviewToUpdate = reviewRepository.findById(id).orElseThrow();
        reviewToUpdate.setContent(review.getContent());

        if (review.getReviewerId() != null) {
            reviewToUpdate.setReviewer(reviewerRepository.getReferenceById(review.getReviewerId()));
        }
        return reviewRepository.save(reviewToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
    }
}
