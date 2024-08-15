package com.epam.jmp.microrecipient.repositories;

import com.epam.jmp.microrecipient.model.MessageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<MessageRecord, Long> {
}
