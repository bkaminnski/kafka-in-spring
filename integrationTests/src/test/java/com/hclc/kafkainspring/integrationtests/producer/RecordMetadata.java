package com.hclc.kafkainspring.integrationtests.producer;

public class RecordMetadata {

    private boolean hasOffset;
    private long offset;
    private int partition;
    private int serializedKeySize;
    private int serializedValueSize;
    private boolean hasTimestamp;
    private long timestamp;
    private String topic;

    public boolean isHasOffset() {
        return hasOffset;
    }

    public void setHasOffset(boolean hasOffset) {
        this.hasOffset = hasOffset;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getSerializedKeySize() {
        return serializedKeySize;
    }

    public void setSerializedKeySize(int serializedKeySize) {
        this.serializedKeySize = serializedKeySize;
    }

    public int getSerializedValueSize() {
        return serializedValueSize;
    }

    public void setSerializedValueSize(int serializedValueSize) {
        this.serializedValueSize = serializedValueSize;
    }

    public boolean isHasTimestamp() {
        return hasTimestamp;
    }

    public void setHasTimestamp(boolean hasTimestamp) {
        this.hasTimestamp = hasTimestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
