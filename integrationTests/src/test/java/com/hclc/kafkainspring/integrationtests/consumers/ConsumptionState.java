package com.hclc.kafkainspring.integrationtests.consumers;

public class ConsumptionState {

    private int remainingQueueLength;

    private ConsumedRecord headOfQueue;

    public int getRemainingQueueLength() {
        return remainingQueueLength;
    }

    public void setRemainingQueueLength(int remainingQueueLength) {
        this.remainingQueueLength = remainingQueueLength;
    }

    public ConsumedRecord getHeadOfQueue() {
        return headOfQueue;
    }

    public void setHeadOfQueue(ConsumedRecord headOfQueue) {
        this.headOfQueue = headOfQueue;
    }
}
