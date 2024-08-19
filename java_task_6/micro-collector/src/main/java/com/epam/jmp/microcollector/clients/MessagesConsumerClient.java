package com.epam.jmp.microcollector.clients;

import com.epam.jmp.microcollector.model.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "messages", url = "${jmp.messages.recipient.url}", path = "/api/v1/")
public interface MessagesConsumerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/messages", produces = "application/json")
    ResponseEntity<List<Message>> getMessagesByFeign();

    @RequestMapping(method = RequestMethod.HEAD, value = "/messages")
    ResponseEntity<Void> getMessagesStatus();
}
