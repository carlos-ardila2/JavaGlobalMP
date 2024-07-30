package com.epam.jmp.akka.model.actors;

import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.epam.jmp.akka.model.PC;

public class RouterActor extends BaseActor {
    public static Props props() {
        return Props.create(RouterActor.class)
                .withRouter(new RoundRobinPool(5));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PC.class, pc -> {
                    getContext().actorOf(MotherboardActor.props()).tell(pc, getSelf());
                    getContext().actorOf(CPUActor.props()).tell(pc, getSelf());
                    getContext().actorOf(RAMActor.props()).tell(pc, getSelf());
                    getContext().actorOf(StorageActor.props()).tell(pc, getSelf());
                })
                .build();
    }
}