package com.hclc.kafkainspring.consumers.assign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.monitoring.consumed.ConsumedRecord;
import com.hclc.kafkainspring.monitoring.errorhandled.ErrorHandledRecord;
import com.hclc.kafkainspring.monitoring.errorhandled.ErrorsCountTracker;
import com.hclc.kafkainspring.monitoring.failablemessages.FailableMessage;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static com.hclc.kafkainspring.monitoring.failablemessages.TypeOfFailure.EXCEPTION_AFTER_CONSUMED;
import static java.lang.Thread.currentThread;

@Component
public class AssignedConsumerStatefulRetryPauseOnError {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ErrorsCountTracker errorsCountTracker;

    private KafkaMessageListenerContainer<String, String> listenerContainer;

    public void consume(ConsumerRecord<String, String> record) {
        long consumedAtMonotonicNano = monotonicNowInNano();
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(consumedAtMonotonicNano, record, failableMessage, currentThread().getName()));
            failableMessage
                    .getTypeOfFailureIfMatching(EXCEPTION_AFTER_CONSUMED)
                    .ifPresent(f -> f.performFailureAction(errorsCountTracker, record, failableMessage.getFailuresCount()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Restore offset for each retry. Once retries are exhausted, commit offset and pause listener container.
     */
    public void handleError(Exception exception, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer) {
        long errorHandledAtMonotonicNano = monotonicNowInNano();
        TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
        if (exception instanceof ExhaustedRetryException) {
            // retries are exhausted, so this time it is time to really handle the error
            errorsCountTracker.remove(record);
            eventPublisher.publishEvent(new ErrorHandledRecord<>(errorHandledAtMonotonicNano, record, exception, currentThread().getName()));
            listenerContainer.pause();
        } else {
            consumer.seek(topicPartition, record.offset());
        }
    }

    public void setListenerContainer(KafkaMessageListenerContainer<String, String> listenerContainer) {
        this.listenerContainer = listenerContainer;
    }
}
