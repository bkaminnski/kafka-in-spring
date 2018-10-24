package com.hclc.kafkainspring.consumers.subscribed;

import com.hclc.kafkainspring.monitoring.errorhandled.ErrorHandledRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.stereotype.Component;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static java.lang.Thread.currentThread;

@Component
public class SubscribedConsumerRebalancedErrorHandler implements ErrorHandler {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void handle(Exception exception, ConsumerRecord<?, ?> record) {
        long errorHandledAtMonotonicNano = monotonicNowInNano();
        eventPublisher.publishEvent(new ErrorHandledRecord<>(errorHandledAtMonotonicNano, record, exception, currentThread().getName()));
    }
}
