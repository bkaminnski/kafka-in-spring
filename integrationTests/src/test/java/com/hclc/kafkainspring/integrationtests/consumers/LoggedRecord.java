package com.hclc.kafkainspring.integrationtests.consumers;

public class LoggedRecord {

    private long loggedAtMonotonicNano;
    private String message;
    private ContainerThreadName containerThreadName;

    public long getLoggedAtMonotonicNano() {
        return loggedAtMonotonicNano;
    }

    public void setLoggedAtMonotonicNano(long loggedAtMonotonicNano) {
        this.loggedAtMonotonicNano = loggedAtMonotonicNano;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setContainerThreadName(String containerThreadName) {
        this.containerThreadName = new ContainerThreadName(containerThreadName);
    }

    public boolean isForConsumerIndex(int consumerIndex) {
        return containerThreadName.isForConsumerIndex(consumerIndex);
    }
}
