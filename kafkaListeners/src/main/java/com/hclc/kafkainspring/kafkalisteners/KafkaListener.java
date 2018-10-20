package com.hclc.kafkainspring.kafkalisteners;

import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class KafkaListener {

    private final List<TopicPartition> topicPartitions;
    private final boolean containerPaused;
    private final boolean autoStartup;
    private final boolean pauseRequested;
    private final boolean running;
    private final String name;
    private final int phase;

    public KafkaListener(KafkaMessageListenerContainer<?, ?> kafkaMessageListenerContainer) {
        this.topicPartitions = Optional.ofNullable(kafkaMessageListenerContainer.getAssignedPartitions())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(TopicPartition::new)
                .collect(toList());
        this.containerPaused = kafkaMessageListenerContainer.isContainerPaused();
        this.autoStartup = kafkaMessageListenerContainer.isAutoStartup();
        this.pauseRequested = kafkaMessageListenerContainer.isPauseRequested();
        this.running = kafkaMessageListenerContainer.isRunning();
        this.name = kafkaMessageListenerContainer.getBeanName();
        this.phase = kafkaMessageListenerContainer.getPhase();
    }

    public Collection<TopicPartition> getTopicPartitions() {
        return topicPartitions;
    }

    public boolean isContainerPaused() {
        return containerPaused;
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public boolean isPauseRequested() {
        return pauseRequested;
    }

    public boolean isRunning() {
        return running;
    }

    public String getName() {
        return name;
    }

    public int getPhase() {
        return phase;
    }
}
