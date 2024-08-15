package com.epam.jmp.microrecipient.model;

import com.epam.jmp.dto.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "messages")
@Data
@NoArgsConstructor
public class MessageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String recipient;
    private LocalDateTime timestamp;

    public MessageRecord(Message message) {
        this.message = message.getMessage();
        this.recipient = message.getRecipient();
        this.timestamp = LocalDateTime.now();
    }
}
