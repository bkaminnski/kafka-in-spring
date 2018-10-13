package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.AssignedConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.AFTER_CONSUMED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;

public class AssignedConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new AssignedConsumer();
        consumer.drain();
    }

    @ParameterizedTest(name = "{index} => topic=''{0}''")
    @ValueSource(strings = {"assignedConsumerStatelessRetryTopic", "assignedConsumerStatefulRetryTopic"})
    void producerProducesAssignedConsumerConsumes_noException(String topic) {
        ProducedRecord produced = producer.produce(topic, NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }

    @ParameterizedTest(name = "{index} => topic=''{0}''")
    @ValueSource(strings = {"assignedConsumerStatelessRetryTopic", "assignedConsumerStatefulRetryTopic"})
    void producerProducesAssignedConsumerConsumes_failOnceNotReaching3TriesLimit_erroHandlerNotInvoked(String topic) {
        ProducedRecord produced = producer.produce(topic, AFTER_CONSUMED, 1);

        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }

    @ParameterizedTest(name = "{index} => topic=''{0}''")
    @ValueSource(strings = {"assignedConsumerStatelessRetryTopic", "assignedConsumerStatefulRetryTopic"})
    void producerProducesAssignedConsumerConsumes_fail3TimesReaching3TriesLimit_exceptionHandledByErroHandler(String topic) {
        ProducedRecord produced = producer.produce(topic, AFTER_CONSUMED, 3);

        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure AFTER_CONSUMED. Attempt 3/3.");
    }

    @ParameterizedTest(name = "{index} => topic=''{0}''")
    @ValueSource(strings = {"assignedConsumerStatelessRetryTopic", "assignedConsumerStatefulRetryTopic"})
    void producerProducesAssignedConsumerConsumes_fail4TimesExceeding3TriesLimit_exceptionHandledByErroHandler(String topic) {
        ProducedRecord produced = producer.produce(topic, AFTER_CONSUMED, 4);

        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure AFTER_CONSUMED. Attempt 3/4.");
    }
}
