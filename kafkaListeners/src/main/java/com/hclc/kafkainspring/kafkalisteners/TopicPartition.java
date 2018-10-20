package com.hclc.kafkainspring.kafkalisteners;

public class TopicPartition {

    private final int partition;
    private final String topic;

    public TopicPartition(org.apache.kafka.common.TopicPartition topicPartition) {
        this.partition = topicPartition.partition();
        this.topic = topicPartition.topic();
    }

    public int getPartition() {
        return partition;
    }

    public String getTopic() {
        return topic;
    }
}
