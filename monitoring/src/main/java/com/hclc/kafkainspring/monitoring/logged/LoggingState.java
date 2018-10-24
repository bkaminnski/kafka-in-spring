package com.hclc.kafkainspring.monitoring.logged;

public class LoggingState {

    private final int remainingQueueLength;

    private final LoggedRecord headOfQueue;

    public LoggingState(int remainingQueueLength, LoggedRecord headOfQueue) {
        this.remainingQueueLength = remainingQueueLength;
        this.headOfQueue = headOfQueue;
    }

    public int getRemainingQueueLength() {
        return remainingQueueLength;
    }

    public LoggedRecord getHeadOfQueue() {
        return headOfQueue;
    }
}
