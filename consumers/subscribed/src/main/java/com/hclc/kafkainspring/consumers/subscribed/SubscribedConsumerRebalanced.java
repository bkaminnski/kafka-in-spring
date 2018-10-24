package com.hclc.kafkainspring.consumers.subscribed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.monitoring.consumed.ConsumedRecord;
import com.hclc.kafkainspring.monitoring.errorhandled.ErrorsCountTracker;
import com.hclc.kafkainspring.monitoring.failablemessages.FailableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static com.hclc.kafkainspring.monitoring.failablemessages.TypeOfFailure.PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT;
import static java.lang.Thread.currentThread;

@Component
public class SubscribedConsumerRebalanced {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ErrorsCountTracker errorsCountTracker;

    @KafkaListener(
            topics = {"subscribedConsumerRebalancedTopic"},
            containerFactory = "rebalancedKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record, KafkaConsumer<?, ?> consumer) {
        long consumedAtMonotonicNano = monotonicNowInNano();
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            failableMessage
                    .getTypeOfFailureIfMatching(PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT)
                    .ifPresent(f -> f.performFailureAction(errorsCountTracker, record, failableMessage.getFailuresCount()));
            eventPublisher.publishEvent(new ConsumedRecord<>(consumedAtMonotonicNano, record, failableMessage, currentThread().getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
