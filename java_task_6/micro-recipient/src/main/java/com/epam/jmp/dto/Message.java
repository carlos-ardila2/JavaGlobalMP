package com.epam.jmp.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    private String message;
    private String recipient;
}