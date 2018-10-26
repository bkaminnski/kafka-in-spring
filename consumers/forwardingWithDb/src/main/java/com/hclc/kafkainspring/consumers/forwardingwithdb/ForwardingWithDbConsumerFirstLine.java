package com.hclc.kafkainspring.consumers.forwardingwithdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hclc.kafkainspring.monitoring.consumed.ConsumedRecord;
import com.hclc.kafkainspring.monitoring.errorhandled.ErrorsCountTracker;
import com.hclc.kafkainspring.monitoring.failablemessages.FailableMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.hclc.kafkainspring.monitoring.MonotonicTimeProvider.monotonicNowInNano;
import static com.hclc.kafkainspring.monitoring.failablemessages.TypeOfFailure.EXCEPTION_AFTER_FORWARDED;
import static java.lang.Thread.currentThread;

@Component
public class ForwardingWithDbConsumerFirstLine {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ErrorsCountTracker errorsCountTracker;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    /**
     * The only way to make producer transaction rollback AND consumption retry is to throw exception.
     * A call to TransactionInterceptor.currentTransactionStatus().setRollbackOnly(); rollbacks producer transaction,
     * but does not cause consumption retry.
     */
    @KafkaListener(topics = "forwardingWithDbConsumerFirstLineTopic")
    @Transactional
    public void consume(ConsumerRecord<String, String> record) {
        long consumedAtMonotonicNano = monotonicNowInNano();
        try {
            FailableMessage failableMessage = new ObjectMapper().readValue(record.value(), FailableMessage.class);
            eventPublisher.publishEvent(new ConsumedRecord<>(consumedAtMonotonicNano, record, failableMessage, currentThread().getName()));
            messageRepository.save(new Message(failableMessage.toString()));
            template.send("forwardingWithDbConsumerSecondLineTopic", failableMessage.getUniqueId(), record.value());
            failableMessage
                    .getTypeOfFailureIfMatching(EXCEPTION_AFTER_FORWARDED)
                    .ifPresent(f -> f.performFailureAction(errorsCountTracker, record, failableMessage.getFailuresCount()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
