package com.hclc.kafkainspring.failablemessages.consumed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ErrorHandledRecord<K, V> {

    @JsonSerialize(using = ConsumerRecordSerializer.class)
    private final ConsumerRecord<K, V> consumerRecord;
    private final Exception exception;

    public ErrorHandledRecord(ConsumerRecord<K, V> consumerRecord, Exception exception) {
        this.consumerRecord = consumerRecord;
        this.exception = exception;
    }

    public ConsumerRecord<K, V> getConsumerRecord() {
        return consumerRecord;
    }

    public Exception getException() {
        return exception;
    }
}
