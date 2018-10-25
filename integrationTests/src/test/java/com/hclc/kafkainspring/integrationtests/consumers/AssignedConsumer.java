package com.hclc.kafkainspring.integrationtests.consumers;

public class AssignedConsumer extends Consumer {

    private static final String ENDPOINT = "http://localhost:8082";

    public AssignedConsumer() {
        super(ENDPOINT);
    }
}
