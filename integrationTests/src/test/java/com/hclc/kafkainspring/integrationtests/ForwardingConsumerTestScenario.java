package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.ForwardingConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;

public class ForwardingConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new ForwardingConsumer();
        consumer.drain();
    }

    @Test
    void succeedInFirstLine_successfullyForwardMessageToSecondLine() {
        ProducedRecord produced = producer.produce("forwardingConsumerFirstLineTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertForwarded(produced, "forwardingConsumerSecondLineTopic");
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }
}
