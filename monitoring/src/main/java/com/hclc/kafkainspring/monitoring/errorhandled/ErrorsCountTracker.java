package com.hclc.kafkainspring.monitoring.errorhandled;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ErrorsCountTracker {

    private final Map<String, AtomicInteger> countByMessageId = new ConcurrentHashMap<>();

    public int incrementAndGetFor(ConsumerRecord<?, ?> record) {
        return countByMessageId
                .computeIfAbsent(uniqueKey(record), k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public void remove(ConsumerRecord<?, ?> record) {
        countByMessageId.remove(uniqueKey(record));
    }

    private String uniqueKey(ConsumerRecord<?, ?> record) {
        return record.topic() + "-" + record.partition() + "-" + record.offset() + "-" + record.key();
    }
}
