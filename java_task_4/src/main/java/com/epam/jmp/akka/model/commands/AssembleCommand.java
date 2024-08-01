package com.epam.jmp.akka.model.commands;

import akka.actor.typed.ActorRef;
import com.epam.jmp.akka.model.PC;

public record AssembleCommand(PC pc, ActorRef<Command> replyTo) implements Command {
}

