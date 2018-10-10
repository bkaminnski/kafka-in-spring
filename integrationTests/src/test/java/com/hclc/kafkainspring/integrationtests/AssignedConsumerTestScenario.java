package com.hclc.kafkainspring.integrationtests;

import com.hclc.kafkainspring.integrationtests.consumerassign.ConsumedRecord;
import com.hclc.kafkainspring.integrationtests.consumerassign.ConsumerAssign;
import com.hclc.kafkainspring.integrationtests.producer.ProducedRecord;
import com.hclc.kafkainspring.integrationtests.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.hclc.kafkainspring.integrationtests.TypeOfFailure.NONE;
import static org.assertj.core.api.Assertions.assertThat;

public class ConsumerAssignTestScenario {

    private Producer producer;
    private ConsumerAssign consumer;

    @BeforeEach
    void before() {
        producer = new Producer();
        consumer = new ConsumerAssign();
        consumer.drain();
    }

    @Test
    void producerProducesConsumerAssignConsumes() {
        ProducedRecord produced = producer.produce("assignedConsumerTopic", NONE);
        ConsumedRecord consumed = consumer.readConsumed();

        assertThat(consumed.getFailableMessage()).isEqualTo(produced.getFailableMessage());
        assertThat(consumed.getConsumerRecord()).isEqualToComparingOnlyGivenFields(produced.getSendResult().getRecordMetadata(),
                "offset", "partition", "serializedKeySize", "serializedValueSize", "timestamp", "topic");
    }
}