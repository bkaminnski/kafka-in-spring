package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.ForwardingConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.EXCEPTION_AFTER_FORWARDED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;

public class ForwardingConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new ForwardingConsumer();
        consumer.drain();
    }

    @Test
    void succeedInFirstLine_successfullyForwardMessageToSecondLine() {
        ProducedRecord produced = producer.produce("forwardingConsumerFirstLineTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertForwarded(produced, "forwardingConsumerSecondLineTopic");
        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();
    }

    @Test
    void failOnceNotReaching3TriesLimit_consumerErrorHandlerNotInvoked_producerErrorHandlerInvoked_forwardMessageToSecondLineOnRetry() {
        ProducedRecord produced = producer.produce("forwardingConsumerFirstLineTopic", EXCEPTION_AFTER_FORWARDED, 1);

        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertConsumedMatchesProduced(produced);
        assertForwarded(produced, "forwardingConsumerSecondLineTopic");
        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();
    }

    @Test
    void fail3TimesReaching3TriesLimit_exceptionsHandledByErrorHandlers_doNotForwardMessageToSecondLine() {
        ProducedRecord produced = producer.produce("forwardingConsumerFirstLineTopic", EXCEPTION_AFTER_FORWARDED, 3);

        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertNoMoreConsumed();
        assertExceptionWasHandled("org.springframework.kafka.listener.ListenerExecutionFailedException", "Simulated failure EXCEPTION_AFTER_FORWARDED. Attempt 3/3.");
    }
}
