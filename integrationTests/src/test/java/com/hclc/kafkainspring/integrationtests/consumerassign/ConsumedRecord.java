package com.hclc.kafkainspring.integrationtests.consumerassign;

import com.hclc.kafkainspring.integrationtests.FailableMessage;

public class ConsumedRecord {

    private ConsumerRecord consumerRecord;
    private FailableMessage failableMessage;

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
}
