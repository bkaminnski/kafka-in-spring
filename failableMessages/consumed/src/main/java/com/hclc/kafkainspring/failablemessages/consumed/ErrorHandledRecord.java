package com.hclc.kafkainspring.failablemessages.consumed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ErrorHandledRecord<K, V> {

    private final long errorHandledAtMonotonicNano;
    @JsonSerialize(using = ConsumerRecordSerializer.class)
    private final ConsumerRecord<K, V> consumerRecord;
    private final HandledException handledException;

    public ErrorHandledRecord(long errorHandledAtMonotonicNano, ConsumerRecord<K, V> consumerRecord, Exception exception) {
        this.errorHandledAtMonotonicNano = errorHandledAtMonotonicNano;
        this.consumerRecord = consumerRecord;
        this.handledException = new HandledException(exception);
    }

    public long getErrorHandledAtMonotonicNano() {
        return errorHandledAtMonotonicNano;
    }

    public ConsumerRecord<K, V> getConsumerRecord() {
        return consumerRecord;
    }

    public HandledException getHandledException() {
        return handledException;
    }
}
