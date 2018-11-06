package com.hclc.kafkainspring.consumers.subscribed;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;

@Configuration
public class SubscribedConsumerRebalancedTopicConfig {

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Autowired
    private SubscribedConsumerRebalancedErrorHandler errorHandler;

    @Autowired
    private SubscribedConsumerRebalancedRebalanceListener rebalanceListener;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> rebalancedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2);
        factory.getContainerProperties().setPollTimeout(250);
        factory.getContainerProperties().setErrorHandler(errorHandler);
        factory.getContainerProperties().setConsumerRebalanceListener(rebalanceListener);
        return factory;
    }

    private ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, FALSE);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // if producer is transactional, make sure to read only committed messages
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 500);
        // From Kafka doc: "the value must be in the allowable range as configured in the broker configuration by group.min.session.timeout.ms and group.max.session.timeout.ms"
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
        // From Kafka doc: "The value must be set lower than session.timeout.ms, but typically should be set no higher than 1/3 of that value."
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 2000);
        return properties;
    }
}