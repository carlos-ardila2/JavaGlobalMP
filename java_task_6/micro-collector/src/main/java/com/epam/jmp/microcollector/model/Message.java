package com.epam.jmp.microcollector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "messages")
public class Message implements Persistable<Long> {
    @Id
    private Long id;
    private String message;
    private String recipient;
    private LocalDateTime timestamp;

    @Override
    public boolean isNew() {
        return true;
    }
}