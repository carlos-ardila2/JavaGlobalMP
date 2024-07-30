package com.epam.jmp.akka.model.actors;

import akka.actor.Props;
import com.epam.jmp.akka.model.PC;

public class StorageActor extends BaseActor {

    private static final String[] STORAGE_CONFIGS = {"512 GB SSD", "10 TB HDD", "1 TB SSD"};

    public static Props props() {
        return Props.create(StorageActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PC.class, pc -> {
                    pc.setStorage(STORAGE_CONFIGS[(int) Math.floor(Math.random() * STORAGE_CONFIGS.length)]);
                    log.info("Storage installed by {}", getSelf());
                    getSender().tell(pc, getSelf());
                })
                .build();
    }
}
