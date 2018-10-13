package com.hclc.kafkainspring.failablemessages.consumed;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import static com.hclc.kafkainspring.failablemessages.consumed.FailureAction.DO_NOTHING;
import static com.hclc.kafkainspring.failablemessages.consumed.FailureAction.THROW_EXCEPTION;

/**
 * Strategy enum pattern; Item 34; Effective Java 3rd Edition
 */
public enum TypeOfFailure {
    AFTER_CONSUMED(THROW_EXCEPTION),
    BEFORE_DB_COMMIT(THROW_EXCEPTION),
    AFTER_DB_COMMIT(THROW_EXCEPTION),
    NONE(DO_NOTHING);

    private final FailureAction failureAction;

    TypeOfFailure(FailureAction failureAction) {
        this.failureAction = failureAction;
    }

    public void performFailureAction(ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures) {
        failureAction.performFailureAction(this, errorsCountTracker, consumerRecord, maxFailures);
    }
}
