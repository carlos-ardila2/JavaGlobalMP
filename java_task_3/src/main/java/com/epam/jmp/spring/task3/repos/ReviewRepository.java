package com.epam.jmp.spring.task3.repos;

import com.epam.jmp.spring.task3.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
