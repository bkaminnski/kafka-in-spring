package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.ForwardingWithDbConsumer;
import com.hclc.kafkainspring.integrationtests.consumers.Message;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;

public class ForwardingWithDbConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new ForwardingWithDbConsumer();
        consumer.drain();
    }

    @Test
    void succeedInFirstLine_successfullyForwardMessageToSecondLineAndSaveToDatabase() {
        ProducedRecord produced = producer.produce("forwardingWithDbConsumerFirstLineTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertForwarded(produced, "forwardingWithDbConsumerSecondLineTopic");
        assertSavedToDatabase(produced);
        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();
    }

    private void assertSavedToDatabase(ProducedRecord produced) {
        Message lastMessage = ((ForwardingWithDbConsumer) consumer).getLastMessage();
        Assertions.assertThat(lastMessage.getContent()).contains(produced.getSendResult().getProducerRecord().getKey());
    }
}
