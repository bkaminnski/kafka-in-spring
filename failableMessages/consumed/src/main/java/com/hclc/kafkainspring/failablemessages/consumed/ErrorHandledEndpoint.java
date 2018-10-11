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

@RestController
public class ErrorHandledEndpoint {

    private BlockingQueue<ErrorHandledRecord<?, ?>> errorHandledRecords = new LinkedBlockingQueue<>();

    @GetMapping("/errorHandled")
    public ResponseEntity<ErrorHandledRecord<?, ?>> getErrorHandled(@RequestParam long timeoutMillis) throws InterruptedException {
        ErrorHandledRecord<?, ?> errorHandledRecord = errorHandledRecords.poll(timeoutMillis, MILLISECONDS);
        return errorHandledRecord == null ? ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build() : ResponseEntity.ok(errorHandledRecord);
    }

    @EventListener
    public void exposeErrorHandledRecord(ErrorHandledRecord<?, ?> errorHandledRecord) throws InterruptedException {
        errorHandledRecords.put(errorHandledRecord);
    }
}
