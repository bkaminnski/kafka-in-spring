package com.hclc.kafkainspring.producer;

import java.util.Optional;

import static java.util.UUID.randomUUID;

public class FailableMessage {

    private final String uniqueId;
    private final TypeOfFailure typeOfFailure;
    private final int failuresCount;

    public FailableMessage(TypeOfFailure typeOfFailure, Optional<Integer> failuresCount) {
        this.uniqueId = randomUUID().toString();
        this.typeOfFailure = typeOfFailure;
        this.failuresCount = failuresCount.orElse(typeOfFailure.getDefaultFailuresCount());
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public TypeOfFailure getTypeOfFailure() {
        return typeOfFailure;
    }

    public int getFailuresCount() {
        return failuresCount;
    }
}
