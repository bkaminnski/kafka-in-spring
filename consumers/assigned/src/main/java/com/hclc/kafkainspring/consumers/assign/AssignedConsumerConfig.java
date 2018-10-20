package com.hclc.kafkainspring.consumers.assign;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ComponentScan({
        "com.hclc.kafkainspring.failablemessages.consumed",
        "com.hclc.kafkainspring.kafkalisteners"
})
public class AssignedConsumerConfig {

}