package com.hclc.kafkainspring.consumers.assign;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;

@Configuration
public class AssignedConsumerStatelessRetryConfig {

    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Autowired
    private AssignedConsumerStatelessRetry assignedConsumer;

    @Bean
    KafkaMessageListenerContainer<String, String> assignedConsumerStatelessRetryListenerContainer() {
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs());
        ContainerProperties containerProperties = new ContainerProperties(topicPartitionInitialOffset());
        containerProperties.setErrorHandler(errorHandler());
        containerProperties.setMessageListener(retryingMessageListenerAdapter());
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, FALSE);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // if producer is transactional, make sure to read only committed messages
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return properties;
    }

    private TopicPartitionInitialOffset topicPartitionInitialOffset() {
        return new TopicPartitionInitialOffset("assignedConsumerStatelessRetryTopic", 0);
    }

    private ErrorHandler errorHandler() {
        return assignedConsumer::handleError;
    }

    private RetryingMessageListenerAdapter<String, String> retryingMessageListenerAdapter() {
        return new RetryingMessageListenerAdapter<>(messageListener(), retryTemplate());
    }

    private MessageListener<String, String> messageListener() {
        return assignedConsumer::consume;
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
}