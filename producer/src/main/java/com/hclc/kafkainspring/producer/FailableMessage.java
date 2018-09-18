package com.hclc.kafkainspring.producer;

import java.util.Optional;

import static java.util.UUID.randomUUID;
import static com.hclc.kafkainspring.producer.TypeOfFailure.AFTER_DB_COMMIT;
import static com.hclc.kafkainspring.producer.TypeOfFailure.BEFORE_DB_COMMIT;

public class FailableMessage {

    private final String uniqueId;
    private final TypeOfFailure typeOfFailure;
    private final int failuresCount;

    public FailableMessage(TypeOfFailure typeOfFailure, Optional<Integer> failuresCount) {
        this.uniqueId = randomUUID().toString();
        this.typeOfFailure = typeOfFailure;
        if (typeOfFailure == BEFORE_DB_COMMIT || typeOfFailure == AFTER_DB_COMMIT) {
            this.failuresCount = failuresCount.orElse(1);
        } else {
            this.failuresCount = 0;
        }
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
