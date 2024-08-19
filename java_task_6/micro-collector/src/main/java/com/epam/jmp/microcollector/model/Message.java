package com.epam.jmp.microcollector.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "messages")
public class Message{
    @Id
    private BigInteger id;
    private String message;
    private String recipient;
    private LocalDateTime timestamp;
}