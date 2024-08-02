package com.epam.jmp.akka.model.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import com.epam.jmp.akka.model.PC;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.CompletedCommand;

public class MotherboardActor extends AbstractBehavior<AssembleCommand> {

    private static final String[] MOTHERBOARDS = {"Gigabyte", "ASRock", "Asus ROG", "MSI", "Apple"};

    public static Behavior<AssembleCommand> create() {
        return Behaviors.setup(MotherboardActor::new);
    }

    private MotherboardActor(ActorContext<AssembleCommand> context) {
        super(context);
    }

    @Override
    public Receive<AssembleCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AssembleCommand.class, this::onInstallMotherboard)
                .build();
    }

    private Behavior<AssembleCommand> onInstallMotherboard(AssembleCommand command) {
        PC pc = command.pc();
        pc.setMotherboard(MOTHERBOARDS[(int) Math.floor(Math.random() * MOTHERBOARDS.length)]);
        getContext().getLog().debug("Motherboard installed by {}", getContext().getSelf());
        command.replyTo().tell(new CompletedCommand(pc));
        return this;
    }
}