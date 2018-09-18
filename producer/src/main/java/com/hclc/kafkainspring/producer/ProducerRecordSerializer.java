package com.hclc.kafkainspring.producer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ProducerRecordSerializer extends JsonSerializer<ProducerRecord<String, String>> {

    @Override
    public void serialize(ProducerRecord<String, String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("headers", value.headers().toArray());
        gen.writeObjectField("key", value.key());
        gen.writeObjectField("partition", value.partition());
        gen.writeObjectField("timestamp", value.timestamp());
        gen.writeObjectField("topic", value.topic());
        gen.writeObjectField("value", value.value());
        gen.writeEndObject();
    }
}
