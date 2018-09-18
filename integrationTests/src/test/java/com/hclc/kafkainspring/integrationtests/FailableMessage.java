package com.hclc.kafkainspring.integrationtests;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FailableMessage that = (FailableMessage) o;

        if (failuresCount != that.failuresCount) return false;
        if (!uniqueId.equals(that.uniqueId)) return false;
        return typeOfFailure == that.typeOfFailure;
    }

    @Override
    public int hashCode() {
        int result = uniqueId.hashCode();
        result = 31 * result + typeOfFailure.hashCode();
        result = 31 * result + failuresCount;
        return result;
    }
}
