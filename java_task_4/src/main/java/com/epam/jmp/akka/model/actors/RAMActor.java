package com.epam.jmp.akka.model.actors;

import akka.actor.Props;
import com.epam.jmp.akka.model.PC;

public class RAMActor extends BaseActor {

    private static final String[] RAM_CONFIGS = {"8Gb", "16Gb", "32Gb"};

    public static Props props() {
        return Props.create(RAMActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PC.class, pc -> {
                    pc.setRam(RAM_CONFIGS[(int) Math.floor(Math.random() * RAM_CONFIGS.length)]);
                    log.info("RAM installed by {}", getSelf());
                    getSender().tell(pc, getSelf());
                })
                .build();
    }
}
