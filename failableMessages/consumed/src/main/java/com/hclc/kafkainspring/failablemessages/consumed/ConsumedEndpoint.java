package com.hclc.kafkainspring.failablemessages.consumed;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@RestController("/consumed")
public class ConsumedEndpoint {

    private BlockingQueue<ConsumedRecord<?, ?>> consumedRecords = new LinkedBlockingQueue<>();

    @GetMapping
    public ResponseEntity<ConsumptionState> getConsumed(@RequestParam long timeoutMillis) throws InterruptedException {
        ConsumedRecord<?, ?> consumedRecord = consumedRecords.poll(timeoutMillis, MILLISECONDS);
        ConsumptionState consumptionState = new ConsumptionState(consumedRecords.size(), consumedRecord);
        return consumedRecord == null ? ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build() : ResponseEntity.ok(consumptionState);
    }

    @EventListener
    public void exposeConsumedRecord(ConsumedRecord<?, ?> consumedRecord) throws InterruptedException {
        consumedRecords.put(consumedRecord);
    }
}
