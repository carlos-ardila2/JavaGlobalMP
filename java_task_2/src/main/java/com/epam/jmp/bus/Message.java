package com.epam.jmp.bus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public record Message(int content, String topic, String sender, LocalDateTime timestamp) {
    @Override
    public String toString() {
        return new StringBuilder("Message [content: ").append(content).append(", ")
                .append("topic: ").append(topic).append(", ")
                .append("sender: ").append(sender).append(", ")
                .append("time: ").append(timestamp.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)))
                .append("]").toString();
    }
}
