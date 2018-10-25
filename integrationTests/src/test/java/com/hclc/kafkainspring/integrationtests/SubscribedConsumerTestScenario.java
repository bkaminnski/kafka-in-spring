package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.ConsumedRecordResponse;
import com.hclc.kafkainspring.integrationtests.consumers.SubscribedConsumer;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT;
import static org.assertj.core.api.Assertions.assertThat;

public class SubscribedConsumerTestScenario extends ConsumerTestScenario {

    private static final long MAX_REBALANCING_WAITING_TIME_MILLIS = 10000;
    private static final long LONG_PROCESSING_WAITING_TIME_MILLIS = 11000;

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new SubscribedConsumer();
        consumer.drain();
    }

    @Test
    void succeedOnFirstAttempt_errorHandlerNotInvoked() {
        ProducedRecord produced = producer.produce("subscribedConsumerSimpleTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();
    }

    @Test
    void whenProcessingTimeDoesNotExceedSessionTimeout_doNotRebalance() {
        ProducedRecord producedToPartition0 = producer.produce("subscribedConsumerRebalancedTopic", NONE, 0, 0);
        assertConsumedByConsumer0(producedToPartition0, 0);

        ProducedRecord producedToPartition1 = producer.produce("subscribedConsumerRebalancedTopic", NONE, 0, 1);
        assertConsumedByConsumer1(producedToPartition1, 0);

        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();
    }

    private void assertConsumedByConsumer0(ProducedRecord producedRecord, long additionalWaitingTime) {
        assertConsumedByConsumer(producedRecord, 0, additionalWaitingTime);
    }

    private void assertConsumedByConsumer1(ProducedRecord producedRecord, long additionalWaitingTime) {
        assertConsumedByConsumer(producedRecord, 1, additionalWaitingTime);
    }

    private void assertConsumedByConsumer(ProducedRecord produced, int consumerIndex, long additionalWaitingTime) {
        ConsumedRecordResponse consumed = assertConsumedMatchesProduced(produced, additionalWaitingTime);
        assertConsumedByConsumerWithIndex(consumed, consumerIndex);
    }

    @Test
    void exceedSessionTimeout_shouldRebalanceAndConsumeByAnotherConsumer() {
        ProducedRecord producedWithLongProcessingTimeToPartition0 = producer.produce("subscribedConsumerRebalancedTopic", PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT, 1, 0);
        ProducedRecord producedWithNoFailureToPartition0 = producer.produce("subscribedConsumerRebalancedTopic", NONE, 0, 0);
        // Even though both messages above were sent to partition 0 and should be handled by consumer 0,
        // they are eventually handled by consumer 1 due processing time exceeding session timeout for the first message.
        assertConsumedByConsumer1(producedWithLongProcessingTimeToPartition0, MAX_REBALANCING_WAITING_TIME_MILLIS);
        assertConsumedByConsumer1(producedWithNoFailureToPartition0, 0);

        // Watch out for that message handled by consumer 0! The real reason for extended processing time might be of external nature,
        // like long time connecting to an overloaded database, long GC pause, network issues.
        // Eventually, when real reason is gone, that forgotten message might have actually executed some code leaving unwanted
        // side effects.
        assertConsumedByConsumer0(producedWithLongProcessingTimeToPartition0, LONG_PROCESSING_WAITING_TIME_MILLIS);
        assertExceptionWasHandledByConsumer0("org.apache.kafka.clients.consumer.CommitFailedException", "Commit cannot be completed since the group has already rebalanced and assigned the partitions to another member.");

        // Wait until Kafka is back to original partitions assignment,
        // so that this test leaves Kafka in the same state as it was before starting the test.
        waitUntilKafkaStateIsBackToOriginalPartitionsAssignment();
    }

    private void assertExceptionWasHandledByConsumer0(String exceptionName, String exceptionMessage) {
        assertExceptionWasHandledByConsumer(exceptionName, exceptionMessage, 0);
    }

    private void waitUntilKafkaStateIsBackToOriginalPartitionsAssignment() {
        boolean partition0AssignedToConsumer0 = consumer.waitUntilLogged("onPartitionsAssigned - partitions: [subscribedConsumerRebalancedTopic-0]", 0);
        assertThat(partition0AssignedToConsumer0).isTrue();
    }
}
