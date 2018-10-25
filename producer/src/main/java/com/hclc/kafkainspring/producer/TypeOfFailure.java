package com.hclc.kafkainspring.producer;

public enum TypeOfFailure {
    EXCEPTION_AFTER_CONSUMED(1), EXCEPTION_AFTER_FORWARDED(1), EXCEPTION_BEFORE_DB_COMMIT(1), EXCEPTION_AFTER_DB_COMMIT(1), PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT(1), NONE(0);

    private final int defaultFailuresCount;

    TypeOfFailure(int defaultFailuresCount) {
        this.defaultFailuresCount = defaultFailuresCount;
    }

    public int getDefaultFailuresCount() {
        return defaultFailuresCount;
    }
}
