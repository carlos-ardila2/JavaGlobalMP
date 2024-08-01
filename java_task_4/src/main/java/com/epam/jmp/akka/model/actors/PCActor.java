package com.epam.jmp.akka.model.actors;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import com.epam.jmp.akka.model.PC;
import com.epam.jmp.akka.model.commands.AssembleCommand;
import com.epam.jmp.akka.model.commands.Command;
import com.epam.jmp.akka.model.commands.CompletedCommand;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PCActor extends AbstractOnMessageBehavior<Command> {

    private final int ASSEMBLE_JOBS_MAP_INITIAL_SIZE = 10;

    // Holds references to the replyTo actors for each PC assembly job
    private final Map<Integer, ActorRef<Command>> assembleJobs = HashMap.newHashMap(ASSEMBLE_JOBS_MAP_INITIAL_SIZE);

    private final ActorRef<AssembleCommand> motherboardActor;
    private final ActorRef<AssembleCommand> cpuActor;
    private final ActorRef<AssembleCommand> ramActor;
    private final ActorRef<AssembleCommand> storageActor;

    // Holds the count of assembled PCs
    private int assembledCount = 0;

    public static Behavior<Command> create() {
        return Behaviors.setup(PCActor::new);
    }

    private PCActor(ActorContext<Command> context) {
        super(context);
        motherboardActor = context.spawn(MotherboardActor.create(), "motherboard-actor");
        cpuActor = context.spawn(CPUActor.create(), "cpu-actor");
        ramActor = context.spawn(RAMActor.create(), "ram-actor");
        storageActor = context.spawn(StorageActor.create(), "storage-actor");
    }

    @Override
    public Behavior<Command> onSignal(Signal signal) {
        if (signal instanceof PostStop) {
            getContext().getSystem().log().info("PCActor stopped: {}", getContext().getSelf());
        }
        return Behaviors.same();
    }

    @Override
    public Behavior<Command> onMessage(Command command) {
        return switch(command) {
            case AssembleCommand assemble -> onAssemblePC(assemble);
            case CompletedCommand complete -> onInstallCompleted(complete);
        };
    }

    private Behavior<Command> onAssemblePC(AssembleCommand command) {
        PC pc = command.pc();
        pc.setId(++assembledCount);
        assembleJobs.put(pc.getId(), command.replyTo());

        motherboardActor.tell(new AssembleCommand(pc, getContext().getSelf()));
        cpuActor.tell(new AssembleCommand(pc, getContext().getSelf()));
        ramActor.tell(new AssembleCommand(pc, getContext().getSelf()));
        storageActor.tell(new AssembleCommand(pc, getContext().getSelf()));

        return Behaviors.same();
    }

    private Behavior<Command> onInstallCompleted(CompletedCommand command) {
        PC pc = command.pc();
        ActorRef<Command> routerActor = assembleJobs.get(pc.getId());
        if (routerActor != null && pc.isComplete()) {
            assembleJobs.put(pc.getId(), null);
            pc.setAssembledAt(LocalDateTime.now());
            getContext().getLog().info("PC assembled by {} at {}", getContext().getSelf(), pc.getAssembledAt());
            routerActor.tell(command);
        }
        return Behaviors.same();
    }
}