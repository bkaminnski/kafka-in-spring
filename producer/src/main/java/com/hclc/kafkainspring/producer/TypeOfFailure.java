package com.hclc.kafkainspring.producer;

public enum TypeOfFailure {
    AFTER_CONSUMED(1), BEFORE_DB_COMMIT(1), AFTER_DB_COMMIT(1), NONE(0);

    private final int defaultFailuresCount;

    TypeOfFailure(int defaultFailuresCount) {
        this.defaultFailuresCount = defaultFailuresCount;
    }

    public int getDefaultFailuresCount() {
        return defaultFailuresCount;
    }
}
