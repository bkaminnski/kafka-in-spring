package com.hclc.kafkainspring.monitoring.failablemessages;

import com.hclc.kafkainspring.monitoring.errorhandled.ErrorsCountTracker;
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
    }, SIMULATE_LONG_PROCESSING_TIME {
        @Override
        void performFailureAction(TypeOfFailure typeOfFailure, ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures) {
            int failuresCount = errorsCountTracker.incrementAndGetFor(consumerRecord);
            if (failuresCount > maxFailures) {
                errorsCountTracker.remove(consumerRecord);
            } else {
                try {
                    Thread.sleep(11000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    };

    abstract void performFailureAction(TypeOfFailure typeOfFailure, ErrorsCountTracker errorsCountTracker, ConsumerRecord<?, ?> consumerRecord, int maxFailures);
}
