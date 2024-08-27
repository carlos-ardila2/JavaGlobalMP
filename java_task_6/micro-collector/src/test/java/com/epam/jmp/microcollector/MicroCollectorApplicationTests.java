package com.epam.jmp.microcollector;

import com.epam.jmp.microcollector.clients.MessagesConsumerClient;
import com.epam.jmp.microcollector.jobs.ScheduledTasks;
import com.epam.jmp.microcollector.model.Message;
import com.epam.jmp.microcollector.repositories.MessagesRepository;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest
class MicroCollectorApplicationTests {

    @MockBean
    MessagesConsumerClient messagesConsumerClient;

    @Mock
    MessagesRepository messagesRepository;

    @SpyBean
    ScheduledTasks tasks;

    @Test
    public void retrieveMessagesJobTest() {

        var response = new ResponseEntity<>(List.of(new Message(BigInteger.ONE, "test", "joe",
                LocalDateTime.now())), HttpStatus.OK);
        doReturn(response).when(messagesConsumerClient).getMessagesByFeign();

        doReturn(List.of()).when(messagesRepository).saveAll(anyList());

        await().atMost(Durations.TEN_SECONDS).untilAsserted(() ->
                verify(tasks, atLeast(2)).retrieveMessages());
    }

}
