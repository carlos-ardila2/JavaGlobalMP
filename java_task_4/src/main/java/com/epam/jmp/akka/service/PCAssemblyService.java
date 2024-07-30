package com.epam.jmp.akka.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import com.epam.jmp.akka.model.actors.RouterActor;
import scala.concurrent.Future;

import com.epam.jmp.akka.model.*;

import java.util.concurrent.CompletableFuture;

public class PCAssemblyService {
    private final ActorSystem system;
    private final ActorRef router;

    public PCAssemblyService() {
        system = ActorSystem.create("PCAssemblySystem");
        router = system.actorOf(RouterActor.props(), "router");
    }

    public CompletableFuture<PC> assemblePC() {
        PC pc = new PC();
        Future<Object> future = Patterns.ask(router, pc, 5000);
        return CompletableFuture.supplyAsync(() -> (PC) future.value().get().get());
    }
}
