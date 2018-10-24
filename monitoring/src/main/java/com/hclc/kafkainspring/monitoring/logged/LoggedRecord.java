package com.hclc.kafkainspring.monitoring.logged;

public class LoggedRecord {

    private final long loggedAtMonotonicNano;
    private final String message;
    private final String containerThreadName;

    public LoggedRecord(long loggedAtMonotonicNano, String message, String containerThreadName) {
        this.loggedAtMonotonicNano = loggedAtMonotonicNano;
        this.message = message;
        this.containerThreadName = containerThreadName;
    }

    public long getLoggedAtMonotonicNano() {
        return loggedAtMonotonicNano;
    }

    public String getMessage() {
        return message;
    }

    public String getContainerThreadName() {
        return containerThreadName;
    }
}
