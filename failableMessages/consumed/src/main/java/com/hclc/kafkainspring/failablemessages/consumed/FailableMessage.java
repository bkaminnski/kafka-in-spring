package com.hclc.kafkainspring.failablemessages.consumed;

public class FailableMessage {

    private String uniqueId;
    private TypeOfFailure typeOfFailure;
    private int failuresCount;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public TypeOfFailure getTypeOfFailure() {
        return typeOfFailure;
    }

    public void setTypeOfFailure(TypeOfFailure typeOfFailure) {
        this.typeOfFailure = typeOfFailure;
    }

    public int getFailuresCount() {
        return failuresCount;
    }

    public void setFailuresCount(int failuresCount) {
        this.failuresCount = failuresCount;
    }

    @Override
    public String toString() {
        return "FailableMessage{" +
                "uniqueId='" + uniqueId + '\'' +
                ", typeOfFailure=" + typeOfFailure +
                ", failuresCount=" + failuresCount +
                '}';
    }
}
