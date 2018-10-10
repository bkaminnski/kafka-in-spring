package com.hclc.kafkainspring.consumers.assign;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ComponentScan("com.hclc.kafkainspring.failablemessages.consumed")
public class AssignedConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Autowired
    private AssignedConsumer assignedConsumer;

    @Bean
    KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer() {
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs());
        TopicPartitionInitialOffset topicPartitionInitialOffset = new TopicPartitionInitialOffset("assignedConsumerTopic", 0);
        ContainerProperties containerProperties = new ContainerProperties(topicPartitionInitialOffset);
        containerProperties.setMessageListener((MessageListener<String, String>) consumerRecord -> assignedConsumer.consume(consumerRecord));
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return properties;
    }
}