package com.hclc.kafkainspring.consumers.subscribed;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ComponentScan("com.hclc.kafkainspring.monitoring")
public class SubscribedConsumerConfig {

}