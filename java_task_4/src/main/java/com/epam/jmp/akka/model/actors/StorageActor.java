package com.epam.jmp.akka.model.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.epam.jmp.akka.model.PC;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.CompletedCommand;

public class StorageActor extends AbstractBehavior<AssembleCommand> {

    private static final String[] STORAGE_CONFIGS = {"512 GB SSD", "10 TB HDD", "1 TB SSD"};


    public static Behavior<AssembleCommand> create() {
        return Behaviors.setup(StorageActor::new);
    }

    private StorageActor(ActorContext<AssembleCommand> context) {
        super(context);
    }

    @Override
    public Receive<AssembleCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AssembleCommand.class, this::onInstallStorage)
                .build();
    }

    private Behavior<AssembleCommand> onInstallStorage(AssembleCommand command) {
        PC pc = command.pc();
        pc.setStorage(STORAGE_CONFIGS[(int) Math.floor(Math.random() * STORAGE_CONFIGS.length)]);
        getContext().getLog().info("Storage installed by {}", getContext().getSelf());
        command.replyTo().tell(new CompletedCommand(pc));
        return this;
    }
}
