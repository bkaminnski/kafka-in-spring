package com.hclc.kafkainspring.monitoring.logged;

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
public class LoggedEndpoint {

    private BlockingQueue<LoggedRecord> loggedRecords = new LinkedBlockingQueue<>();

    @GetMapping("/logged")
    public ResponseEntity<LoggingState> getLogged(@RequestParam long timeoutMillis) throws InterruptedException {
        LoggedRecord loggedRecord = loggedRecords.poll(timeoutMillis, MILLISECONDS);
        LoggingState loggingState = new LoggingState(loggedRecords.size(), loggedRecord);
        return loggedRecord == null ? ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build() : ResponseEntity.ok(loggingState);
    }

    @GetMapping("/logged/preview")
    public ResponseEntity<List<LoggedRecord>> preview() throws InterruptedException {
        return ResponseEntity.ok(new ArrayList<>(loggedRecords));
    }

    @EventListener
    public void exposeLoggedRecord(LoggedRecord loggedRecord) throws InterruptedException {
        loggedRecords.put(loggedRecord);
    }
}
