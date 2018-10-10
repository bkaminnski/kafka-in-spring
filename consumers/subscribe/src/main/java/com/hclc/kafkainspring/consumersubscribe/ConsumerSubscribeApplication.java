package com.hclc.kafkainspring.consumersubscribe;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class ConsumerSubscribeApplication {

    private static Logger logger = LoggerFactory.getLogger(ConsumerSubscribeApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ConsumerSubscribeApplication.class, args);
    }

    @KafkaListener(topics = {"test", "test2", "test3"})
    public void listen(ConsumerRecord<?, ?> cr) throws Exception {
        logger.info(cr.toString());
    }
}
