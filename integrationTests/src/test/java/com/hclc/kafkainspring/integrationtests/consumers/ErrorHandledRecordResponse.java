package com.hclc.kafkainspring.integrationtests.consumers;

public class ErrorHandledRecordResponse  extends Response {

    private final ErrorHandledRecord errorHandledRecord;

    public ErrorHandledRecordResponse(int statusCode, ErrorHandledRecord errorHandledRecord) {
        super(statusCode);
        this.errorHandledRecord = errorHandledRecord;
    }

    public ErrorHandledRecord getErrorHandledRecord() {
        return errorHandledRecord;
    }
}
