package com.hclc.kafkainspring.failablemessages.consumed;

public class ConsumptionState {

    private final int remainingQueueLength;

    private final ConsumedRecord<?, ?> headOfQueue;

    public ConsumptionState(int remainingQueueLength, ConsumedRecord<?, ?> headOfQueue) {
        this.remainingQueueLength = remainingQueueLength;
        this.headOfQueue = headOfQueue;
    }

    public int getRemainingQueueLength() {
        return remainingQueueLength;
    }

    public ConsumedRecord<?, ?> getHeadOfQueue() {
        return headOfQueue;
    }
}
