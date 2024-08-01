package com.epam.jmp.akka.model.commands;

import com.epam.jmp.akka.model.PC;

public record CompletedCommand(PC pc) implements Command {
}

