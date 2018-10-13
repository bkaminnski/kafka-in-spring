package com.hclc.kafkainspring.consumers.assign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.failablemessages.consumed.ConsumedRecord;
import com.hclc.kafkainspring.failablemessages.consumed.ErrorHandledRecord;
import com.hclc.kafkainspring.failablemessages.consumed.FailableMessage;
import com.hclc.kafkainspring.failablemessages.consumed.TypeOfFailure;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.hclc.kafkainspring.failablemessages.consumed.TypeOfFailure.AFTER_CONSUMED;

@Component
public class AssignedConsumer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void consume(ConsumerRecord<String, String> record) {
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(record, failableMessage));
            failableMessage
                    .getTypeOfFailureIfMatching(AFTER_CONSUMED)
                    .ifPresent(TypeOfFailure::performFailureAction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleError(Exception exception, ConsumerRecord<?, ?> consumerRecord, Consumer<?, ?> c) {
        // TopicPartition topicPartition = new TopicPartition(consumerRecord.topic(), consumerRecord.partition());
        // ConsumerAwareErrorHandler interface allows to manually adjust offset (below, restores the offset to replay failed message)
        // c.seek(topicPartition, consumerRecord.offset());
        eventPublisher.publishEvent(new ErrorHandledRecord<>(consumerRecord, exception));
    }
}
