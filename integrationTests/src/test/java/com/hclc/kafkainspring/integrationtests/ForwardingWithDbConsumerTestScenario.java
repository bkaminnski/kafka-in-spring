package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumers.ForwardingWithDbConsumer;
import com.hclc.kafkainspring.integrationtests.consumers.Message;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.EXCEPTION_AFTER_FORWARDED;
import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static org.assertj.core.api.Assertions.assertThat;

public class ForwardingWithDbConsumerTestScenario extends ConsumerTestScenario {

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new ForwardingWithDbConsumer();
        consumer.drain();
    }

    @Test
    void succeedInFirstLine_successfullyForwardMessageToSecondLineAndSaveToDatabase() {
        ProducedRecord produced = producer.produce("forwardingWithDbConsumerFirstLineTopic", NONE, 0);

        assertConsumedMatchesProduced(produced);
        assertForwarded(produced, "forwardingWithDbConsumerSecondLineTopic");
        assertSavedToDatabase(produced);
        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();
    }

    private void assertSavedToDatabase(ProducedRecord produced) {
        Message lastMessage = ((ForwardingWithDbConsumer) consumer).getLastMessage();
        assertThat(lastMessage.getContent()).contains(produced.getSendResult().getProducerRecord().getKey());
    }

    @Test
    void failOnceNotReaching3TriesLimit_consumerErrorHandlerNotInvoked_producerErrorHandlerInvoked_forwardMessageAndSaveToDatabaseToSecondLineOnRetry() {
        Message referenceMessage = ((ForwardingWithDbConsumer) consumer).generateReferenceMessage();

        ProducedRecord produced = producer.produce("forwardingWithDbConsumerFirstLineTopic", EXCEPTION_AFTER_FORWARDED, 1);

        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertConsumedMatchesProduced(produced);
        assertForwarded(produced, "forwardingWithDbConsumerSecondLineTopic");
        assertNoMoreConsumed();
        assertNoMoreExceptionsHandled();

        Message lastMessage = ((ForwardingWithDbConsumer) consumer).getLastMessage();
        assertNumberOfDatabaseTransactionsRollbacks(1, referenceMessage, lastMessage);
    }

    /**
     * Even when a database transaction is rolled back, primary key sequence is incremented. This allows to assert on the number of "skipped" ids.
     * Each "skipped" id corresponds to a single rolled back transaction.
     */
    private void assertNumberOfDatabaseTransactionsRollbacks(long expectedNumberOfRollbacks, Message referenceMessage, Message lastMessage) {
        assertThat(referenceMessage.numberOfIdsSkippedComparedTo(lastMessage)).isEqualTo(expectedNumberOfRollbacks);
    }

    @Test
    void fail3TimesReaching3TriesLimit_exceptionsHandledByErrorHandlers_doNotForwardMessageToSecondLine_doNotSaveToDb() {
        Message referenceMessage = ((ForwardingWithDbConsumer) consumer).generateReferenceMessage();

        ProducedRecord produced = producer.produce("forwardingWithDbConsumerFirstLineTopic", EXCEPTION_AFTER_FORWARDED, 3);

        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertConsumedMatchesProduced(produced);
        assertExceptionWasHandled("org.apache.kafka.common.KafkaException", "Failing batch since transaction was aborted");
        assertNoMoreConsumed();
        assertExceptionWasHandled("org.springframework.kafka.listener.ListenerExecutionFailedException", "Simulated failure EXCEPTION_AFTER_FORWARDED. Attempt 3/3.");

        Message lastMessage = ((ForwardingWithDbConsumer) consumer).getLastMessage();
        assertNoMessageWasSaved(referenceMessage, lastMessage);

        Message messageCreatedAfterTestScenario = ((ForwardingWithDbConsumer) consumer).generateReferenceMessage();
        assertNumberOfDatabaseTransactionsRollbacks(3, referenceMessage, messageCreatedAfterTestScenario);
    }

    private void assertNoMessageWasSaved(Message referenceMessage, Message lastMessage) {
        assertThat(lastMessage).isEqualTo(referenceMessage);
    }
}
