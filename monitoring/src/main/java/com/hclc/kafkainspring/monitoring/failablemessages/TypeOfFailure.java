package com.hclc.kafkainspring.monitoring.failablemessages;

import com.hclc.kafkainspring.monitoring.errorhandled.ErrorsCountTracker;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Strategy enum pattern; Item 34; Effective Java 3rd Edition
 */
public enum TypeOfFailure {
    EXCEPTION_AFTER_CONSUMED(FailureAction.THROW_EXCEPTION),
    EXCEPTION_BEFORE_DB_COMMIT(FailureAction.THROW_EXCEPTION),
    EXCEPTION_AFTER_DB_COMMIT(FailureAction.THROW_EXCEPTION),
    PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT(FailureAction.SIMULATE_LONG_PROCESSING_TIME),
    NONE(FailureAction.DO_NOTHING);

    private final FailureAction failureAction;

    TypeOfFailure(FailureAction failureAction) {
        this.failureAction = failureAction;
    }

    public void performFailureAction(ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures) {
        failureAction.performFailureAction(this, errorsCountTracker, consumerRecord, maxFailures);
    }
}
