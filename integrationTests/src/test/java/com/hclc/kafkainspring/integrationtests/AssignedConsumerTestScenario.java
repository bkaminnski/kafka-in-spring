package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.AssignedConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.AFTER_CONSUMED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;

public class AssignedConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new AssignedConsumer();
        consumer.drain();
    }

    @ParameterizedTest(name = "{index} => topic=''{0}'', additionalIntervalMillisForPolling={1}")
    @CsvSource({
            "assignedConsumerStatelessRetryTopic, 0",
            "assignedConsumerStatefulRetryTopic, 300"
    })
    void producerProducesAssignedConsumerConsumes_noException(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, NONE, 0);

        assertConsumedMatchesProduced(produced, new ArrayList<>(), additionalIntervalMillisForPolling);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
    }

    @ParameterizedTest(name = "{index} => topic=''{0}'', additionalIntervalMillisForPolling={1}")
    @CsvSource({
            "assignedConsumerStatelessRetryTopic, 0",
            "assignedConsumerStatefulRetryTopic, 300"
    })
    void producerProducesAssignedConsumerConsumes_failOnceNotReaching3TriesLimit_erroHandlerNotInvoked(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, AFTER_CONSUMED, 1);

        List<Long> consumedAtMonotonicNano = new ArrayList<>();
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertNoMoreConsumed();
        assertNoExceptionWasHandled();
        assertElapsedTimeBetweenHandlingRecords(consumedAtMonotonicNano, additionalIntervalMillisForPolling, 100);
    }

    @ParameterizedTest(name = "{index} => topic=''{0}'', additionalIntervalMillisForPolling={1}")
    @CsvSource({
            "assignedConsumerStatelessRetryTopic, 0",
            "assignedConsumerStatefulRetryTopic, 300"
    })
    void producerProducesAssignedConsumerConsumes_fail3TimesReaching3TriesLimit_exceptionHandledByErroHandler(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, AFTER_CONSUMED, 3);

        List<Long> consumedAtMonotonicNano = new ArrayList<>();
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure AFTER_CONSUMED. Attempt 3/3.", consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertElapsedTimeBetweenHandlingRecords(consumedAtMonotonicNano, additionalIntervalMillisForPolling, 100, 200, 0);
    }

    @ParameterizedTest(name = "{index} => topic=''{0}'', additionalIntervalMillisForPolling={1}")
    @CsvSource({
            "assignedConsumerStatelessRetryTopic, 0",
            "assignedConsumerStatefulRetryTopic, 300"
    })
    void producerProducesAssignedConsumerConsumes_fail4TimesExceeding3TriesLimit_exceptionHandledByErroHandler(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, AFTER_CONSUMED, 4);

        List<Long> consumedAtMonotonicNano = new ArrayList<>();
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure AFTER_CONSUMED. Attempt 3/4.", consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertElapsedTimeBetweenHandlingRecords(consumedAtMonotonicNano, additionalIntervalMillisForPolling, 100, 200, 0);
    }
}
