package com.hclc.kafkainspring.monitoring.errorhandled;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@RestController
public class ErrorHandledEndpoint {

    private BlockingQueue<ErrorHandledRecord<?, ?>> errorHandledRecords = new LinkedBlockingQueue<>();

    @GetMapping("/errorHandled")
    public ResponseEntity<ErrorHandlingState> getErrorHandled(@RequestParam long timeoutMillis) throws InterruptedException {
        ErrorHandledRecord<?, ?> errorHandledRecord = errorHandledRecords.poll(timeoutMillis, MILLISECONDS);
        ErrorHandlingState errorHandlingState = new ErrorHandlingState(errorHandledRecords.size(), errorHandledRecord);
        return errorHandledRecord == null ? ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build() : ResponseEntity.ok(errorHandlingState);
    }

    @GetMapping("/errorHandled/preview")
    public ResponseEntity<List<ErrorHandledRecord<?, ?>>> preview() throws InterruptedException {
        return ResponseEntity.ok(new ArrayList<>(errorHandledRecords));
    }

    @EventListener
    public void exposeErrorHandledRecord(ErrorHandledRecord<?, ?> errorHandledRecord) throws InterruptedException {
        errorHandledRecords.put(errorHandledRecord);
    }
}
