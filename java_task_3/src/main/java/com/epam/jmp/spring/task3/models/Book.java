package com.epam.jmp.spring.task3.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "published_date", columnDefinition = "DATE")
    private LocalDate publishedDate;

    @JsonIgnore
    @ManyToOne
    @EqualsAndHashCode.Exclude private Author author;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude private Set<Chapter> chapters;

    public boolean isChaptersPresent() {
        return chapters != null && !chapters.isEmpty();
    }

    public String getAuthorName() {
        return author.getName();
    }

    public int getReviewCount() {
        return chapters.stream().mapToInt(Chapter::getReviewCount).sum();
    }

    public String getChaptersTitles() {
        return chapters.stream().map(Chapter::getTitle).reduce((a, b) -> a + "\n" + b).orElse("");
    }
}
