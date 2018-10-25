package com.hclc.kafkainspring.consumers.forwarding;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ForwardingConsumerConsumerConfig {

    private static Logger logger = LoggerFactory.getLogger(ForwardingConsumerConsumerConfig.class);

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Autowired
    private ForwardingConsumerConsumerErrorHandler errorHandler;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs());
        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConsumerFactory(consumerFactory);
        listenerContainerFactory.setRetryTemplate(retryTemplate());
        listenerContainerFactory.getContainerProperties().setErrorHandler(errorHandler);
        listenerContainerFactory.setConcurrency(1);
        return listenerContainerFactory;
    }

    private RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // retry up to 3 times
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));

        // start with 100ms and increase each time 2x until reaching 400ms, all other retries with 400ms
        ExponentialBackOffPolicy exponential = new ExponentialBackOffPolicy();
        exponential.setInitialInterval(100);
        exponential.setMultiplier(2);
        exponential.setMaxInterval(400);
        retryTemplate.setBackOffPolicy(exponential);

        return retryTemplate;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }
}