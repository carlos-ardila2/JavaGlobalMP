package com.epam.jmp.akka.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.epam.jmp.akka.model.actors.PCActor;

import com.epam.jmp.akka.model.PC;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.Command;
import com.epam.jmp.akka.model.commands.CompletedCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class PCAssemblyService {

    public final ActorSystem<Command> system;
    private final ActorRef<Command> router;

    public PCAssemblyService() {
        this(1.0);
    }

    public PCAssemblyService(double parallelism) {
        Config config = ConfigFactory.load();

        if (parallelism != 1.0) {
            Config parallelismConfig = ConfigFactory.parseString(
                    "akka.default-dispatcher.fork-join-executor.parallelism-factor=" + parallelism);
            config = parallelismConfig.withFallback(config);
        }

        system = ActorSystem.create(PCActor.create(), "router", config);
        router = system;
    }

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
