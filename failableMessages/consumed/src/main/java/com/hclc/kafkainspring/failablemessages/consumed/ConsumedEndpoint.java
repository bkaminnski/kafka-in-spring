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
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController("/consumed")
public class ConsumedEndpoint {

    private BlockingQueue<ConsumedRecord> consumedRecords = new LinkedBlockingQueue<>();

    @GetMapping
    public ResponseEntity<ConsumedRecord> getConsumed(@RequestParam long timeoutMillis) throws InterruptedException {
        ConsumedRecord consumedRecord = consumedRecords.poll(timeoutMillis, MILLISECONDS);
        return consumedRecord == null ? ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build() : ResponseEntity.ok(consumedRecord);
    }

    @EventListener
    public void exposeFailableMesssage(ConsumedRecord consumedRecord) throws InterruptedException {
        consumedRecords.put(consumedRecord);
    }
}
