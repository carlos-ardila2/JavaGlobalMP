package com.epam.jmp.spring.task3.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude private Book book;

    @OneToMany(mappedBy = "bookChapter", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude private Set<Review> reviews;

    public boolean isReviewsPresent() {
        return getReviewCount() > 0;
    }

    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }
}
