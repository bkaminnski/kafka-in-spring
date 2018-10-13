package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.*;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ConsumerTestScenario {

    Producer producer;
    Consumer consumer;

    void assertConsumedMatchesProduced(ProducedRecord produced) {
        ConsumedRecordResponse consumed = consumer.readConsumed();
        assertThat(consumed.isOk()).isTrue();
        ConsumedRecord consumedRecord = consumed.getConsumptionState().getHeadOfQueue();
        assertThat(consumedRecord.getFailableMessage()).isEqualTo(produced.getFailableMessage());
        assertThat(consumedRecord.getConsumerRecord()).isEqualToComparingOnlyGivenFields(produced.getSendResult().getRecordMetadata(),
                "offset", "partition", "serializedKeySize", "serializedValueSize", "timestamp", "topic");
    }

    void assertNoExceptionWasHandled() {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled();
        assertThat(errorHandled.isTimedOut()).isTrue();
    }

    void assertNoMoreConsumed() {
        ConsumedRecordResponse consumed = consumer.readConsumed();
        assertThat(consumed.isTimedOut()).isTrue();
    }

    void assertExceptionWasHandled(String exceptionMessage) {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled();
        assertThat(errorHandled.isOk()).isTrue();
        ErrorHandledRecord errorHandledRecord = errorHandled.getErrorHandlingState().getHeadOfQueue();
        assertThat(errorHandledRecord.getException().getMessage()).isEqualTo(exceptionMessage);
    }
}
