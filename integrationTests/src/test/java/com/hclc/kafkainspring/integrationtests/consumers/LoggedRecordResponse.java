package com.hclc.kafkainspring.integrationtests.consumers;

public class LoggedRecordResponse extends Response {

    private final ErrorHandlingState errorHandlingState;

    public LoggedRecordResponse(int statusCode, ErrorHandlingState errorHandlingState) {
        super(statusCode);
        this.errorHandlingState = errorHandlingState;
    }

    public ErrorHandlingState getErrorHandlingState() {
        return errorHandlingState;
    }
}
