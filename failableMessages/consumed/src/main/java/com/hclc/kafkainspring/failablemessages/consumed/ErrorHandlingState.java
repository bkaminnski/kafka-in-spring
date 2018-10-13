package com.hclc.kafkainspring.failablemessages.consumed;

public class ErrorHandlingState {

    private final int remainingQueueLength;

    private final ErrorHandledRecord<?, ?> headOfQueue;

    public ErrorHandlingState(int remainingQueueLength, ErrorHandledRecord<?, ?> headOfQueue) {
        this.remainingQueueLength = remainingQueueLength;
        this.headOfQueue = headOfQueue;
    }

    public int getRemainingQueueLength() {
        return remainingQueueLength;
    }

    public ErrorHandledRecord<?, ?> getHeadOfQueue() {
        return headOfQueue;
    }
}
