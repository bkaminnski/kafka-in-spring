package com.hclc.kafkainspring.integrationtests.consumers;

public class LoggingState {

    private int remainingQueueLength;

    private LoggedRecord headOfQueue;

    public int getRemainingQueueLength() {
        return remainingQueueLength;
    }

    public void setRemainingQueueLength(int remainingQueueLength) {
        this.remainingQueueLength = remainingQueueLength;
    }

    public LoggedRecord getHeadOfQueue() {
        return headOfQueue;
    }

    public void setHeadOfQueue(LoggedRecord headOfQueue) {
        this.headOfQueue = headOfQueue;
    }
}
