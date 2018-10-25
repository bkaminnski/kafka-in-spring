package com.hclc.kafkainspring.integrationtests.consumers;

public class SubscribedConsumer extends Consumer {

    private static final String ENDPOINT = "http://localhost:8083";

    public SubscribedConsumer() {
        super(ENDPOINT);
    }
}
