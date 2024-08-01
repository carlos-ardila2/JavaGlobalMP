package com.epam.jmp.akka.model.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.epam.jmp.akka.model.PC;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.CompletedCommand;

public class RAMActor extends AbstractBehavior<AssembleCommand> {

    private static final String[] RAM_CONFIGS = {"8Gb", "16Gb", "32Gb"};

    public static Behavior<AssembleCommand> create() {
        return Behaviors.setup(RAMActor::new);
    }

    private RAMActor(ActorContext<AssembleCommand> context) {
        super(context);
    }

    @Override
    public Receive<AssembleCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AssembleCommand.class, this::onInstallRAM)
                .build();
    }

    private Behavior<AssembleCommand> onInstallRAM(AssembleCommand command) {
        PC pc = command.pc();
        pc.setRam(RAM_CONFIGS[(int) Math.floor(Math.random() * RAM_CONFIGS.length)]);
        getContext().getLog().info("RAM memory installed by {}", getContext().getSelf());
        command.replyTo().tell(new CompletedCommand(pc));
        return this;
    }
}
