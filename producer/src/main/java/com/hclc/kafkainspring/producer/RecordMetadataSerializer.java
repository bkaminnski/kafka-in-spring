package com.hclc.kafkainspring.producer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class RecordMetadataSerializer extends JsonSerializer<RecordMetadata> {

    @Override
    public void serialize(RecordMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("hasOffset", value.hasOffset());
        gen.writeObjectField("offset", value.offset());
        gen.writeObjectField("partition", value.partition());
        gen.writeObjectField("serializedKeySize", value.serializedKeySize());
        gen.writeObjectField("serializedValueSize", value.serializedValueSize());
        gen.writeObjectField("hasTimestamp", value.hasTimestamp());
        gen.writeObjectField("timestamp", value.timestamp());
        gen.writeObjectField("topic", value.topic());
        gen.writeEndObject();
    }
}
