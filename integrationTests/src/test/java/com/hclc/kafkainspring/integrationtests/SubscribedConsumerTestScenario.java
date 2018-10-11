package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.ConsumedRecordResponse;
import com.hclc.kafkainspring.integrationtests.consumers.ErrorHandledRecordResponse;
import com.hclc.kafkainspring.integrationtests.consumers.SubscribedConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static org.assertj.core.api.Assertions.assertThat;

public class SubscribedConsumerTestScenario {

    private Producer producer;
    private SubscribedConsumer consumer;

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new SubscribedConsumer();
        consumer.drain();
    }

    @Test
    void producerProducesSubscribedConsumerConsumes() {
        ProducedRecord produced = producer.produce("subscribedConsumerTopic", NONE);

        assertConsumedMatchesProduced(produced);
        assertNoExceptionWasHandled();
    }

    private void assertConsumedMatchesProduced(ProducedRecord produced) {
        ConsumedRecordResponse consumed = consumer.readConsumed();
        assertThat(consumed.isOk()).isTrue();
        assertThat(consumed.getConsumedRecord().getFailableMessage()).isEqualTo(produced.getFailableMessage());
        assertThat(consumed.getConsumedRecord().getConsumerRecord()).isEqualToComparingOnlyGivenFields(produced.getSendResult().getRecordMetadata(),
                "offset", "partition", "serializedKeySize", "serializedValueSize", "timestamp", "topic");
    }

    private void assertNoExceptionWasHandled() {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled();
        assertThat(errorHandled.isTimedOut()).isTrue();
    }
}
