package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.*;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ConsumerTestScenario {

    private static final long MAX_ACCEPTABLE_OVERHEAD_MILLIS = 125;
    private static final long NANO_TO_MILLIS = 1000 * 1000;

    Producer producer;
    Consumer consumer;

    ConsumedRecordResponse assertConsumedMatchesProduced(ProducedRecord produced) {
        return assertConsumedMatchesProduced(produced, new ArrayList<>(), 0);
    }

    ConsumedRecordResponse assertConsumedMatchesProduced(ProducedRecord produced, long additionalIntervalMillisForPolling) {
        return assertConsumedMatchesProduced(produced, new ArrayList<>(), additionalIntervalMillisForPolling);
    }

    ConsumedRecordResponse assertConsumedMatchesProduced(ProducedRecord produced, List<Long> consumedAtMonotonicNano, long additionalIntervalMillisForPolling) {
        ConsumedRecordResponse consumed = consumer.readConsumed(additionalIntervalMillisForPolling);
        assertThat(consumed.isOk()).isTrue();
        ConsumedRecord consumedRecord = consumed.getConsumptionState().getHeadOfQueue();
        assertThat(consumedRecord.getFailableMessage()).isEqualTo(produced.getFailableMessage());
        assertThat(consumedRecord.getConsumerRecord()).isEqualToComparingOnlyGivenFields(produced.getSendResult().getRecordMetadata(),
                "offset", "partition", "serializedKeySize", "serializedValueSize", "timestamp", "topic");

        consumedAtMonotonicNano.add(consumedRecord.getConsumedAtMonotonicNano());

        return consumed;
    }

    ConsumedRecordResponse assertForwarded(ProducedRecord produced, String toTopic) {
        ConsumedRecordResponse consumed = consumer.readConsumed(0);
        assertThat(consumed.isOk()).isTrue();
        ConsumedRecord consumedRecord = consumed.getConsumptionState().getHeadOfQueue();
        assertThat(consumedRecord.getFailableMessage()).isEqualTo(produced.getFailableMessage());
        assertThat(consumedRecord.getConsumerRecord()).isEqualToComparingOnlyGivenFields(produced.getSendResult().getRecordMetadata(),
                "partition", "serializedKeySize", "serializedValueSize");
        assertThat(consumedRecord.getConsumerRecord().getTopic()).isEqualTo(toTopic);

        return consumed;
    }

    void assertNoMoreExceptionsHandled() {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled(0);
        assertThat(errorHandled.isTimedOut()).isTrue();
    }

    void assertNoMoreConsumed() {
        ConsumedRecordResponse consumed = consumer.readConsumed(0);
        assertThat(consumed.isTimedOut()).isTrue();
    }

    ErrorHandledRecord assertExceptionWasHandledByConsumer(String exceptionName, String exceptionMessage, int consumerIndex) {
        ErrorHandledRecord errorHandledRecord = assertExceptionWasHandled(exceptionName, exceptionMessage, new ArrayList<>(), 0);
        assertThat(errorHandledRecord.isForConsumerIndex(consumerIndex)).isTrue();
        return errorHandledRecord;
    }

    ErrorHandledRecord assertExceptionWasHandled(String exceptionName, String exceptionMessage) {
        return assertExceptionWasHandled(exceptionName, exceptionMessage, new ArrayList<>(), 0);
    }

    ErrorHandledRecord assertExceptionWasHandled(String exceptionName, String exceptionMessage, List<Long> consumedAtMonotonicNano, long additionalIntervalMillisForPolling) {
        ErrorHandledRecordResponse errorHandled = consumer.readErrorHandled(additionalIntervalMillisForPolling);
        assertThat(errorHandled.isOk()).isTrue();
        ErrorHandledRecord errorHandledRecord = errorHandled.getErrorHandlingState().getHeadOfQueue();
        assertThat(errorHandledRecord.getHandledException().getName()).isEqualTo(exceptionName);
        assertThat(errorHandledRecord.getHandledException().getMessage()).contains(exceptionMessage);

        consumedAtMonotonicNano.add(errorHandledRecord.getErrorHandledAtMonotonicNano());
        return errorHandledRecord;
    }

    void assertElapsedTimeBetweenHandlingRecords(List<Long> consumedAtMonotonicNano, long additionalIntervalMillisForPolling, long... expectedElapsedTimeMillis) {
        for (int i = 1; i < consumedAtMonotonicNano.size(); i++) {
            long actualElapsedTimeMillis = (consumedAtMonotonicNano.get(i) - consumedAtMonotonicNano.get(i - 1)) / NANO_TO_MILLIS;
            long minExpectedElapsedTimeMillis = additionalIntervalMillisForPolling + expectedElapsedTimeMillis[i - 1];
            long maxExpectedElapsedTimeMillis = minExpectedElapsedTimeMillis + MAX_ACCEPTABLE_OVERHEAD_MILLIS;
            assertThat(actualElapsedTimeMillis).isBetween(minExpectedElapsedTimeMillis, maxExpectedElapsedTimeMillis);
        }
    }

    void assertConsumedByConsumerWithIndex(ConsumedRecordResponse consumedRecordResponse, int consumerIndex) {
        assertThat(consumedRecordResponse.getConsumptionState().getHeadOfQueue().isForConsumerIndex(consumerIndex)).isTrue();
    }
}
