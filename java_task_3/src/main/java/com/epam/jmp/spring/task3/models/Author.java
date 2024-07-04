package com.epam.jmp.spring.task3.models;

import lombok.*;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude private Set<Book> books;

    public boolean isBooksPresent() {
        return getBookCount() > 0;
    }

    public int getBookCount() {
        return books != null ? books.size() : 0;
    }
}
