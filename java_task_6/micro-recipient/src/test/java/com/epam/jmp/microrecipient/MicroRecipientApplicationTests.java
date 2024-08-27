package com.epam.jmp.microrecipient;

import com.epam.jmp.dto.Message;
import com.epam.jmp.microrecipient.model.MessageRecord;
import com.epam.jmp.microrecipient.repositories.MessagesRepository;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import org.junit.jupiter.api.Test;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.rabbit.test.context.SpringRabbitTest;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@SpringRabbitTest
@SpringBootTest(classes = { MicroRecipientApplicationTests.RabbitTestConfiguration.class },
        properties = "spring.main.allow-bean-definition-overriding=true")
class MicroRecipientApplicationTests {

    static final String QUEUE_NAME = "messages";
    static final String LISTENER_ID = "messagesReceiver";

    @SpyBean
    MessagesRepository messagesRepository;

    @Autowired
    private TestRabbitTemplate template;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Test
    void testMessageReception() {

        Message message = new Message("test", "joe");
        template.convertAndSend(QUEUE_NAME, message);

        try {
            RabbitListenerTestHarness.InvocationData invocationData =
                    harness.getNextInvocationDataFor(LISTENER_ID, 0, TimeUnit.SECONDS);

            // Verify that the message was received by the listener
            assertNotNull(invocationData);
            assertThat(invocationData.getArguments()[0], equalTo(message));
            // Already consumed
            assertNull(invocationData.getResult());

            // Verify that the message was saved in the repository
            verify(messagesRepository, timeout(1000)).save(isA(MessageRecord.class));
            verify(messagesRepository, timeout(1000)).save(argThat(record ->
                    record.getMessage().equals(message.getMessage())));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration
    @RabbitListenerTest(capture = true)
    public static class RabbitTestConfiguration {

        @Bean
        public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
            return new Jackson2JsonMessageConverter();
        }

        @Bean
        @Primary
        public TestRabbitTemplate testRabbitTemplate(
                ConnectionFactory mockConnectionFactory,
                Jackson2JsonMessageConverter jackson2JsonMessageConverter
        ) {
            TestRabbitTemplate testRabbitTemplate = new TestRabbitTemplate(mockConnectionFactory);
            testRabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
            return testRabbitTemplate;
        }

        @Bean
        @Primary
        public ConnectionFactory mockConnectionFactory() throws IOException {
            ConnectionFactory factory = mock(ConnectionFactory.class);
            Connection connection = mock(Connection.class);
            Channel channel = mock(Channel.class);
            AMQP.Queue.DeclareOk declareOk = mock(AMQP.Queue.DeclareOk.class);
            willReturn(connection).given(factory).createConnection();
            willReturn(channel).given(connection).createChannel(anyBoolean());
            given(channel.isOpen()).willReturn(true);
            given(channel.queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyMap()))
                    .willReturn(declareOk);
            return factory;
        }

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setMessageConverter(jackson2JsonMessageConverter);
            return factory;
        }

        @Bean
        public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
            return new SimpleMessageListenerContainer(connectionFactory);
        }
    }
}
