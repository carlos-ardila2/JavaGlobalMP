package com.epam.jmp.akka.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.epam.jmp.akka.model.actors.PCActor;

import com.epam.jmp.akka.model.*;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.Command;
import com.epam.jmp.akka.model.commands.CompletedCommand;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class PCAssemblyService {

    private final ActorSystem<Command> system = ActorSystem.create(PCActor.create(), "router");
    private final ActorRef<Command> router = system;

    public CompletableFuture<PC> assemblePC() {
        PC pc = new PC();
        CompletionStage<Command> result =
                AskPattern.ask(
                        router,
                        replyTo -> new AssembleCommand(pc, replyTo),
                        Duration.ofSeconds(3),
                        system.scheduler());

        return result.thenApply(command -> ((CompletedCommand) command).pc()).toCompletableFuture();
    }

    public void shutdown() {
        system.terminate();
    }
}
