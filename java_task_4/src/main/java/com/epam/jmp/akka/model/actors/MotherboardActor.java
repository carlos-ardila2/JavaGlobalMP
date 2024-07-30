package com.epam.jmp.akka.model.actors;

import akka.actor.Props;
import com.epam.jmp.akka.model.PC;

public class MotherboardActor extends BaseActor {

    private static final String[] MOTHERBOARDS = {"Gigabyte", "ASRock", "Asus ROG", "MSI", "Apple"};

    public static Props props() {
        return Props.create(MotherboardActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PC.class, pc -> {
                    pc.setMotherboard(MOTHERBOARDS[(int) Math.floor(Math.random() * MOTHERBOARDS.length)]);
                    log.info("Motherboard installed by {}", getSelf());
                    getSender().tell(pc, getSelf());
                })
                .build();
    }
}
