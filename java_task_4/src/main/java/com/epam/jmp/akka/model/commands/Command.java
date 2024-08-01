package com.epam.jmp.akka.model.commands;

public sealed interface Command permits AssembleCommand, CompletedCommand {
}
