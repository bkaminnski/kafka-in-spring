package com.hclc.kafkainspring.consumerassign;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class ConsumerRecordSerializer extends JsonSerializer<ConsumerRecord<String, String>> {

    @Override
    public void serialize(ConsumerRecord<String, String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("headers", value.headers().toArray());
        gen.writeObjectField("key", value.key());
        gen.writeObjectField("offset", value.offset());
        gen.writeObjectField("partition", value.partition());
        gen.writeObjectField("serializedKeySize", value.serializedKeySize());
        gen.writeObjectField("serializedValueSize", value.serializedValueSize());
        gen.writeObjectField("timestamp", value.timestamp());
        gen.writeObjectField("timestampType", value.timestampType());
        gen.writeObjectField("topic", value.topic());
        gen.writeObjectField("value", value.value());
        gen.writeEndObject();
    }
}
