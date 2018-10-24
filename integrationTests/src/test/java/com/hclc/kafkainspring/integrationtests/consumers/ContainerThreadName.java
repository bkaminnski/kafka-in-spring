package com.hclc.kafkainspring.integrationtests.consumers;

class ContainerThreadName {

    private final String name;

    ContainerThreadName(String name) {
        this.name = name;
    }

    boolean isForConsumerIndex(int consumerIndex) {
        return name.endsWith(consumerIndex + "-C-1");
    }
}
