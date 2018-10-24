package com.hclc.kafkainspring.consumers.subscribed;

import com.hclc.kafkainspring.monitoring.logged.LoggedRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static java.lang.Thread.currentThread;

@Component
public class SubscribedConsumerRebalancedRebalanceListener implements ConsumerAwareRebalanceListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        eventPublisher.publishEvent(new LoggedRecord(monotonicNowInNano(), "onPartitionsRevokedBeforeCommit - partitions: " + partitions, currentThread().getName()));
    }

    @Override
    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        eventPublisher.publishEvent(new LoggedRecord(monotonicNowInNano(), "onPartitionsRevokedAfterCommit - partitions: " + partitions, currentThread().getName()));
    }

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        eventPublisher.publishEvent(new LoggedRecord(monotonicNowInNano(), "onPartitionsAssigned - partitions: " + partitions, currentThread().getName()));
    }
}
