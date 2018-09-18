package com.hclc.kafkainspring.integrationtests.producer;

public class SendResult<K, V> {

    private ProducerRecord<K, V> producerRecord;
    private RecordMetadata recordMetadata;

    public ProducerRecord<K, V> getProducerRecord() {
        return producerRecord;
    }

    public void setProducerRecord(ProducerRecord<K, V> producerRecord) {
        this.producerRecord = producerRecord;
    }

    public RecordMetadata getRecordMetadata() {
        return recordMetadata;
    }

    public void setRecordMetadata(RecordMetadata recordMetadata) {
        this.recordMetadata = recordMetadata;
    }
}
