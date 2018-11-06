package com.hclc.kafkainspring.consumers.forwarding;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ForwardingConsumerProducerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.client-id}")
    private String clientId;

    @Autowired
    private ForwardingConsumerProducerListener producerListener;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        DefaultKafkaProducerFactory<String, String> kafkaProducerFactory = new DefaultKafkaProducerFactory<>(producerConfigs());
        // required for the producer to be transactional; underneath configures "transactional.id" producer configuration property
        kafkaProducerFactory.setTransactionIdPrefix("forwardingTX");
        return kafkaProducerFactory;
    }

    @Bean
    public KafkaTransactionManager<String, String> transactionManager(ProducerFactory<String, String> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        // highest guarantee of ordering; anything > 1 risks reordering in case of failed delivery
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        // wait for all replicas to acknowledge the message
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // avoid duplicates
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        return properties;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setProducerListener(producerListener);
        return kafkaTemplate;
    }
}