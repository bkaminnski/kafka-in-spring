package com.hclc.kafkainspring.consumers.assign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.failablemessages.consumed.ConsumedRecord;
import com.hclc.kafkainspring.failablemessages.consumed.ErrorHandledRecord;
import com.hclc.kafkainspring.failablemessages.consumed.ErrorsCountTracker;
import com.hclc.kafkainspring.failablemessages.consumed.FailableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.hclc.kafkainspring.failablemessages.consumed.MonotonicTimeProvider.monotonicNowInNano;
import static com.hclc.kafkainspring.failablemessages.consumed.TypeOfFailure.AFTER_CONSUMED;

@Component
public class AssignedConsumerStatelessRetry {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ErrorsCountTracker errorsCountTracker;

    public void consume(ConsumerRecord<String, String> record) {
        long consumedAtMonotonicNano = monotonicNowInNano();
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(consumedAtMonotonicNano, record, failableMessage));
            failableMessage
                    .getTypeOfFailureIfMatching(AFTER_CONSUMED)
                    .ifPresent(f -> f.performFailureAction(errorsCountTracker, record, failableMessage.getFailuresCount()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleError(Exception exception, ConsumerRecord<?, ?> record) {
        long errorHandledAtMonotonicNano = monotonicNowInNano();
        errorsCountTracker.remove(record);
        eventPublisher.publishEvent(new ErrorHandledRecord<>(errorHandledAtMonotonicNano, record, exception));
    }
}
