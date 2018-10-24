package com.hclc.kafkainspring.monitoring.errorhandled;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HandledException {

    private final String name;
    private final String message;
    private final String cause;
    private final String causeMessage;
    private final String stacktrace;

    public HandledException(Exception exception) {
        name = exception.getClass().getName();
        message = exception.getMessage();
        cause = exception.getCause() == null ? null : exception.getCause().getClass().getName();
        causeMessage = exception.getCause() == null ? null : exception.getCause().getMessage();
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        exception.printStackTrace(printWriter);
        stacktrace = writer.toString();
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getCause() {
        return cause;
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    public String getStacktrace() {
        return stacktrace;
    }
}
