package com.hclc.kafkainspring.integrationtests.consumers;

public class ForwardingConsumer extends Consumer {

    private static final String ENDPOINT = "http://localhost:8084";

    public ForwardingConsumer() {
        super(ENDPOINT);
    }
}
