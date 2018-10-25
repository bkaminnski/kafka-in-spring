package com.hclc.kafkainspring.consumers.forwarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ForwardingConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForwardingConsumerApplication.class, args);
    }
}
