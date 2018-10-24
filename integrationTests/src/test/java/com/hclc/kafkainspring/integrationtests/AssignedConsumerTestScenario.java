package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.AssignedConsumer;
import com.hclc.kafkainspring.integrationtests.consumers.ConsumedRecordResponse;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.EXCEPTION_AFTER_CONSUMED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.glassfish.grizzly.http.util.HttpStatus.OK_200;

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
    void succeedOnFirstAttempt_errorHandlerNotInvoked(String topic, long additionalIntervalMillisForPolling) {
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
    void failOnceNotReaching3TriesLimit_errorHandlerNotInvoked(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, EXCEPTION_AFTER_CONSUMED, 1);

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
    void fail3TimesReaching3TriesLimit_exceptionHandledByErrorHandler(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, EXCEPTION_AFTER_CONSUMED, 3);

        List<Long> consumedAtMonotonicNano = new ArrayList<>();
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure EXCEPTION_AFTER_CONSUMED. Attempt 3/3.", consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertElapsedTimeBetweenHandlingRecords(consumedAtMonotonicNano, additionalIntervalMillisForPolling, 100, 200, 0);
    }

    @ParameterizedTest(name = "{index} => topic=''{0}'', additionalIntervalMillisForPolling={1}")
    @CsvSource({
            "assignedConsumerStatelessRetryTopic, 0",
            "assignedConsumerStatefulRetryTopic, 300"
    })
    void fail4TimesExceeding3TriesLimit_exceptionHandledByErrorHandler(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, EXCEPTION_AFTER_CONSUMED, 4);

        List<Long> consumedAtMonotonicNano = new ArrayList<>();
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertConsumedMatchesProduced(produced, consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertNoMoreConsumed();
        assertExceptionWasHandled("Simulated failure EXCEPTION_AFTER_CONSUMED. Attempt 3/4.", consumedAtMonotonicNano, additionalIntervalMillisForPolling);
        assertElapsedTimeBetweenHandlingRecords(consumedAtMonotonicNano, additionalIntervalMillisForPolling, 100, 200, 0);
    }

    @ParameterizedTest(name = "{index} => topic=''{0}'', additionalIntervalMillisForPolling={1}")
    @CsvSource({
            "assignedConsumerStatelessRetryTopic, 0",
            "assignedConsumerStatefulRetryTopic, 300"
    })
    void continueConsumptionAfterHandlingFailure(String topic, long additionalIntervalMillisForPolling) {
        fail3TimesReaching3TriesLimit_exceptionHandledByErrorHandler(topic, additionalIntervalMillisForPolling);
        succeedOnFirstAttempt_errorHandlerNotInvoked(topic, additionalIntervalMillisForPolling);
    }

    /**
     * Uses dedicated topic, for which listener container is paused in case of error.
     */
    @Test
    void pauseConsumptionAfterHandlingFailureAndConsumeAfterResumed() {
        String topic = "assignedConsumerStatefulRetryPauseOnErrorTopic";
        String listenerContainer = "assignedConsumerStatefulRetryPauseOnErrorListenerContainer";
        long additionalIntervalMillisForPolling = 300;

        fail3TimesReaching3TriesLimit_exceptionHandledByErrorHandler(topic, additionalIntervalMillisForPolling);
        ProducedRecord producedRecord = doNotConsumeProducedRecordAfterConsumptionIsPaused(topic, additionalIntervalMillisForPolling);
        resumeConsumption(listenerContainer);
        eventuallyConsumeProducedRecordAfterConsumptionIsResumed(producedRecord, additionalIntervalMillisForPolling);
    }

    private ProducedRecord doNotConsumeProducedRecordAfterConsumptionIsPaused(String topic, long additionalIntervalMillisForPolling) {
        ProducedRecord produced = producer.produce(topic, NONE, 0);

        ConsumedRecordResponse consumed = consumer.readConsumed(additionalIntervalMillisForPolling);
        assertThat(consumed.isTimedOut()).isTrue();
        return produced;
    }

    private void resumeConsumption(String listenerContainer) {
        int status = consumer.resumeConsumptionOn(listenerContainer);
        assertThat(status).isEqualTo(OK_200.getStatusCode());
        boolean containerIsRunning = consumer.waitUntilListenerIsRunning(listenerContainer);
        assertThat(containerIsRunning).isTrue();
    }

    private void eventuallyConsumeProducedRecordAfterConsumptionIsResumed(ProducedRecord produced, long additionalIntervalMillisForPolling) {
        assertConsumedMatchesProduced(produced, new ArrayList<>(), additionalIntervalMillisForPolling);
    }
}
