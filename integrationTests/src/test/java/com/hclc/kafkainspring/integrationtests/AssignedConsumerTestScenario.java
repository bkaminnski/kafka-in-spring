package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.AssignedConsumer;
import com.hclc.kafkainspring.integrationtests.consumers.ConsumedRecordResponse;
import com.hclc.kafkainspring.integrationtests.consumers.Consumer;
import com.hclc.kafkainspring.integrationtests.consumers.ErrorHandledRecordResponse;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.AFTER_CONSUMED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static org.assertj.core.api.Assertions.assertThat;

public class AssignedConsumerTestScenario {

    private Producer producer;
    private Consumer consumer;

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new AssignedConsumer();
        consumer.drain();
    }

    @Test
    void producerProducesAssignedConsumerConsumes_noException() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", NONE);

        assertConsumedMatchesProduced(produced);
        assertNoExceptionWasHandled();
    }

    @Test
    void producerProducesAssignedConsumerConsumes_exceptionHandledByErroHandler() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", AFTER_CONSUMED);

        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled();
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

    private void assertExceptionWasHandled() {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled();
        assertThat(errorHandled.isOk()).isTrue();
        assertThat(errorHandled.getErrorHandledRecord().getException().getMessage()).isEqualTo("Simulated failure AFTER_CONSUMED");
    }

}
