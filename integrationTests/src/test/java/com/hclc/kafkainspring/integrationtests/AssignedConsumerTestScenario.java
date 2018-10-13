package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.*;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.AFTER_CONSUMED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static org.assertj.core.api.Assertions.assertThat;

public class AssignedConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new AssignedConsumer();
        consumer.drain();
    }

    @Test
    void producerProducesAssignedConsumerConsumes_noException() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }

    @Test
    void producerProducesAssignedConsumerConsumes_failOnceNotReaching3TriesLimit_erroHandlerNotInvoked() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", AFTER_CONSUMED, 1);

        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }

    @Test
    void producerProducesAssignedConsumerConsumes_fail3TimesReaching3TriesLimit_exceptionHandledByErroHandler() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", AFTER_CONSUMED, 3);

        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure AFTER_CONSUMED. Attempt 3/3.");
    }

    @Test
    void producerProducesAssignedConsumerConsumes_fail4TimesExceeding3TriesLimit_exceptionHandledByErroHandler() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", AFTER_CONSUMED, 4);

        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure AFTER_CONSUMED. Attempt 3/4.");
    }
}
