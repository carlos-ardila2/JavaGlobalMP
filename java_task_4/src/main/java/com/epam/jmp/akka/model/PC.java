package com.epam.jmp.akka.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PC {
    private String motherboard;
    private String cpu;
    private String ram;
    private String storage;
    private LocalDateTime assembledAt;
    private int id;

    public boolean isComplete() {
        return motherboard != null && cpu != null && ram != null && storage != null;
    }
}
