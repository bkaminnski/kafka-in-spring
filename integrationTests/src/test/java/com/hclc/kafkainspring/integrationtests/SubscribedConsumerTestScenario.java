package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.SubscribedConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;

public class SubscribedConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new SubscribedConsumer();
        consumer.drain();
    }

    @Test
    void producerProducesSubscribedConsumerConsumes() {
        ProducedRecord produced = producer.produce("subscribedConsumerTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }
}
