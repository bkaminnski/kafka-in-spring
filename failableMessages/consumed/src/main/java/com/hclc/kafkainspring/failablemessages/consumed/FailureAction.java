package com.hclc.kafkainspring.failablemessages.consumed;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Strategy enum type; Item 34; Effective Java 3rd Edition
 */
public enum FailureAction {
    DO_NOTHING {
        @Override
        void performFailureAction(TypeOfFailure typeOfFailure, ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures) {
            // do nothing
        }
    }, THROW_EXCEPTION {
        @Override
        void performFailureAction(TypeOfFailure typeOfFailure, ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures) {
            int failuresCount = errorsCountTracker.incrementAndGetFor(consumerRecord);
            if (failuresCount > maxFailures) {
                errorsCountTracker.remove(consumerRecord);
            } else {
                throw new RuntimeException("Simulated failure " + typeOfFailure + ". Attempt " + failuresCount + "/" + maxFailures + ".");
            }
        }
    };

    abstract void performFailureAction(TypeOfFailure typeOfFailure, ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures);
}
