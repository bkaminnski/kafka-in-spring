package com.hclc.kafkainspring.integrationtests.consumers;

public class ErrorHandledRecord {

    private long errorHandledAtMonotonicNano;
    private ConsumerRecord consumerRecord;
    private Exception exception;

    public long getErrorHandledAtMonotonicNano() {
        return errorHandledAtMonotonicNano;
    }

    public void setErrorHandledAtMonotonicNano(long errorHandledAtMonotonicNano) {
        this.errorHandledAtMonotonicNano = errorHandledAtMonotonicNano;
    }

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
