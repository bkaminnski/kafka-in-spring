package com.hclc.kafkainspring.integrationtests.consumers;

public class ErrorHandlingState {

    private int remainingQueueLength;

    private ErrorHandledRecord headOfQueue;

    public int getRemainingQueueLength() {
        return remainingQueueLength;
    }

    public void setRemainingQueueLength(int remainingQueueLength) {
        this.remainingQueueLength = remainingQueueLength;
    }

    public ErrorHandledRecord getHeadOfQueue() {
        return headOfQueue;
    }

    public void setHeadOfQueue(ErrorHandledRecord headOfQueue) {
        this.headOfQueue = headOfQueue;
    }
}
