package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.*;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ConsumerTestScenario {

    private static final long MAX_ACCEPTABLE_OVERHEAD_MILLIS = 75;
    private static final long NANO_TO_MILLIS = 1000 * 1000;

    Producer producer;
    Consumer consumer;

    void assertConsumedMatchesProduced(ProducedRecord produced, List<Long> consumedAtMonotonicNano, long additionalIntervalMillisForPolling) {
        ConsumedRecordResponse consumed = consumer.readConsumed(additionalIntervalMillisForPolling);
        assertThat(consumed.isOk()).isTrue();
        ConsumedRecord consumedRecord = consumed.getConsumptionState().getHeadOfQueue();
        assertThat(consumedRecord.getFailableMessage()).isEqualTo(produced.getFailableMessage());
        assertThat(consumedRecord.getConsumerRecord()).isEqualToComparingOnlyGivenFields(produced.getSendResult().getRecordMetadata(),
                "offset", "partition", "serializedKeySize", "serializedValueSize", "timestamp", "topic");

        consumedAtMonotonicNano.add(consumedRecord.getConsumedAtMonotonicNano());
    }

    void assertNoExceptionWasHandled() {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled(0);
        assertThat(errorHandled.isTimedOut()).isTrue();
    }

    void assertNoMoreConsumed() {
        ConsumedRecordResponse consumed = consumer.readConsumed(0);
        assertThat(consumed.isTimedOut()).isTrue();
    }

    void assertExceptionWasHandled(String exceptionMessage, List<Long> consumedAtMonotonicNano, long additionalIntervalMillisForPolling) {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled(additionalIntervalMillisForPolling);
        assertThat(errorHandled.isOk()).isTrue();
        ErrorHandledRecord errorHandledRecord = errorHandled.getErrorHandlingState().getHeadOfQueue();
        assertThat(errorHandledRecord.getHandledException().getMessage()).contains(exceptionMessage);

        consumedAtMonotonicNano.add(errorHandledRecord.getErrorHandledAtMonotonicNano());
    }

    void assertElapsedTimeBetweenHandlingRecords(List<Long> consumedAtMonotonicNano, long additionalIntervalMillisForPolling, long... expectedElapsedTimeMillis) {
        for (int i = 1; i < consumedAtMonotonicNano.size(); i++) {
            long actualElapsedTimeMillis = (consumedAtMonotonicNano.get(i) - consumedAtMonotonicNano.get(i - 1)) / NANO_TO_MILLIS;
            long minExpectedElapsedTimeMillis = additionalIntervalMillisForPolling + expectedElapsedTimeMillis[i - 1];
            long maxExpectedElapsedTimeMillis = minExpectedElapsedTimeMillis + MAX_ACCEPTABLE_OVERHEAD_MILLIS;
            assertThat(actualElapsedTimeMillis).isBetween(minExpectedElapsedTimeMillis, maxExpectedElapsedTimeMillis);
        }
    }
}
