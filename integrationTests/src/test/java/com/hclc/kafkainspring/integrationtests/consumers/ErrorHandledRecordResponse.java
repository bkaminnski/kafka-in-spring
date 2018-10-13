package com.hclc.kafkainspring.integrationtests.consumers;

public class ErrorHandledRecordResponse extends Response {

    private final ErrorHandlingState errorHandlingState;

    public ErrorHandledRecordResponse(int statusCode, ErrorHandlingState errorHandlingState) {
        super(statusCode);
        this.errorHandlingState = errorHandlingState;
    }

    public ErrorHandlingState getErrorHandlingState() {
        return errorHandlingState;
    }
}
