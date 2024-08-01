package com.epam.jmp.akka.model.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import com.epam.jmp.akka.model.PC;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.CompletedCommand;

public class CPUActor extends AbstractBehavior<AssembleCommand> {

    private static final String[] CPUS = {"Intel Core i7", "AMD Ryzen 9", "Apple Silicon M2"};

    public static Behavior<AssembleCommand> create() {
        return Behaviors.setup(CPUActor::new);
    }

    private CPUActor(ActorContext<AssembleCommand> context) {
        super(context);
    }

    @Override
    public Receive<AssembleCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(AssembleCommand.class, this::onInstallCPU)
                .build();
    }

    private Behavior<AssembleCommand> onInstallCPU(AssembleCommand command) {
        PC pc = command.pc();
        pc.setCpu(CPUS[(int) Math.floor(Math.random() * CPUS.length)]);
        getContext().getLog().info("CPU installed by {}", getContext().getSelf());
        command.replyTo().tell(new CompletedCommand(pc));
        return this;
    }
}
