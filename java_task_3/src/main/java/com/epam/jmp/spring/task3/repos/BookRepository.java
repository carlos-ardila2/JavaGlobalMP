package com.epam.jmp.spring.task3.repos;

import com.epam.jmp.spring.task3.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByPublishedDateAfter(LocalDate date);
}
