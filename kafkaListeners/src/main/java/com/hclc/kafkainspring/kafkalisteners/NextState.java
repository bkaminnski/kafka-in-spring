package com.hclc.kafkainspring.kafkalisteners;

import org.springframework.kafka.listener.KafkaMessageListenerContainer;

public enum NextState {

    RUNNING {
        @Override
        public KafkaMessageListenerContainer<?, ?> perform(KafkaMessageListenerContainer<?, ?> kafkaMessageListenerContainer) {
            if (!kafkaMessageListenerContainer.isContainerPaused()) {
                throw new IllegalStateException("Kafka listener is already running.");
            }
            kafkaMessageListenerContainer.resume();
            return kafkaMessageListenerContainer;
        }
    }, PAUSED {
        @Override
        public KafkaMessageListenerContainer<?, ?> perform(KafkaMessageListenerContainer<?, ?> kafkaMessageListenerContainer) {
            if (kafkaMessageListenerContainer.isContainerPaused()) {
                throw new IllegalStateException("Kafka listener is already paused.");
            }
            kafkaMessageListenerContainer.pause();
            return kafkaMessageListenerContainer;
        }
    };

    public abstract KafkaMessageListenerContainer<?, ?> perform(KafkaMessageListenerContainer<?, ?> kafkaMessageListenerContainer);
}
