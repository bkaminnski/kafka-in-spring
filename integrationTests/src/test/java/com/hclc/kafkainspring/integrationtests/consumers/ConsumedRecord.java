package com.hclc.kafkainspring.integrationtests.consumers;

import com.hclc.kafkainspring.integrationtests.FailableMessage;

public class ConsumedRecord {

    private long consumedAtMonotonicNano;
    private ConsumerRecord consumerRecord;
    private FailableMessage failableMessage;
    private ContainerThreadName containerThreadName;

    public long getConsumedAtMonotonicNano() {
        return consumedAtMonotonicNano;
    }

    public void setConsumedAtMonotonicNano(long consumedAtMonotonicNano) {
        this.consumedAtMonotonicNano = consumedAtMonotonicNano;
    }

    public ConsumerRecord getConsumerRecord() {
        return consumerRecord;
    }

    public void setConsumerRecord(ConsumerRecord consumerRecord) {
        this.consumerRecord = consumerRecord;
    }

    public FailableMessage getFailableMessage() {
        return failableMessage;
    }

    public void setFailableMessage(FailableMessage failableMessage) {
        this.failableMessage = failableMessage;
    }

    public void setContainerThreadName(String containerThreadName) {
        this.containerThreadName = new ContainerThreadName(containerThreadName);
    }

    public boolean isForConsumerIndex(int consumerIndex) {
        return containerThreadName.isForConsumerIndex(consumerIndex);
    }
}
