package com.epam.jmp.spring.task3.repos;

import com.epam.jmp.spring.task3.models.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewerRepository extends JpaRepository<Reviewer, Long> {
}
