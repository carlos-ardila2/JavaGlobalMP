package com.epam.jmp.akka.model.actors;

import akka.actor.Props;
import com.epam.jmp.akka.model.PC;

public class CPUActor extends BaseActor {

    private static final String[] CPUS = {"Intel Core i7", "AMD Ryzen 9", "Apple Silicon M2"};

    public static Props props() {
        return Props.create(CPUActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PC.class, pc -> {
                    pc.setCpu(CPUS[(int) Math.floor(Math.random() * CPUS.length)]);
                    log.info("CPU installed by {}", getSelf());
                    getSender().tell(pc, getSelf());
                })
                .build();
    }
}
