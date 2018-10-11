package com.hclc.kafkainspring.failablemessages.consumed;

/**
 * Strategy enum type; Item 34; Effective Java 3rd Edition
 */
public enum FailureAction {
    DO_NOTHING {
        @Override
        void performFailureAction(TypeOfFailure typeOfFailure) {
            // do nothing
        }
    }, THROW_EXCEPTION {
        @Override
        void performFailureAction(TypeOfFailure typeOfFailure) {
            throw new RuntimeException("Simulated failure " + typeOfFailure);
        }
    };

    abstract void performFailureAction(TypeOfFailure typeOfFailure);
}
