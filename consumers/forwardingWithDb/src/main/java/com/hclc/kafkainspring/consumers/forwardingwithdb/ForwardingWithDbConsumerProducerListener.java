package com.hclc.kafkainspring.consumers.forwardingwithdb;

import com.hclc.kafkainspring.monitoring.errorhandled.ErrorHandledRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static java.lang.Thread.currentThread;

@Component
public class ForwardingWithDbConsumerProducerListener implements ProducerListener<String, String> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onError(ProducerRecord producerRecord, Exception exception) {
        long errorHandledAtMonotonicNano = monotonicNowInNano();
        eventPublisher.publishEvent(new ErrorHandledRecord<>(errorHandledAtMonotonicNano, null, exception, currentThread().getName()));
    }
}
