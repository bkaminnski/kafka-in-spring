package com.hclc.kafkainspring.integrationtests.consumers;

public class ErrorHandledRecord {

    private ConsumerRecord consumerRecord;
    private Exception exception;

    public ConsumerRecord getConsumerRecord() {
        return consumerRecord;
    }

    public void setConsumerRecord(ConsumerRecord consumerRecord) {
        this.consumerRecord = consumerRecord;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
