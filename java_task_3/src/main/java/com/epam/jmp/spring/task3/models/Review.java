package com.epam.jmp.spring.task3.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude private Chapter bookChapter;

    @Column(name = "book_chapter_id", insertable = false, updatable = false)
    private Long bookChapterId;

    @JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude private Reviewer reviewer;

    @Column(name = "reviewer_id", insertable = false, updatable = false)
    private Long reviewerId;

    public String getReviewerName() {
        return reviewer.getName();
    }
}
