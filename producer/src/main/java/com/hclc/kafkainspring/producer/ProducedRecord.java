package com.hclc.kafkainspring.producer;

import org.springframework.kafka.support.SendResult;

public class ProducedRecord<K, V> {

    private final SendResult<K, V> sendResult;
    private final FailableMessage failableMessage;

    public ProducedRecord(SendResult<K, V> sendResult, FailableMessage failableMessage) {
        this.sendResult = sendResult;
        this.failableMessage = failableMessage;
    }

    public SendResult<K, V> getSendResult() {
        return sendResult;
    }

    public FailableMessage getFailableMessage() {
        return failableMessage;
    }
}
