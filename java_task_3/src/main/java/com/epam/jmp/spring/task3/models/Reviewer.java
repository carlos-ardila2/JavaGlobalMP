package com.epam.jmp.spring.task3.models;

import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Entity
@Data
public class Reviewer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "reviewer")
    @EqualsAndHashCode.Exclude private Set<Review> reviews;

    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }

    public boolean isReviewsPresent() {
        return getReviewCount() > 0;
    }
}
