package com.hclc.kafkainspring.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController("/produce")
public class ProducerEndpoint {

    @Autowired
    private KafkaTemplate<String, String> template;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ProducedRecord<String, String> produce(
            @RequestParam("toTopic") String toTopic,
            @RequestParam("typeOfFailure") TypeOfFailure typeOfFailure,
            @RequestParam(value = "failuresCount", required = false) Optional<Integer> failuresCount
    ) throws ExecutionException, InterruptedException, JsonProcessingException {
        FailableMessage failableMessage = new FailableMessage(typeOfFailure, failuresCount);
        String payload = new ObjectMapper().writeValueAsString(failableMessage);
        ListenableFuture<SendResult<String, String>> sendResultListenable = template.send(toTopic, failableMessage.getUniqueId(), payload);
        SendResult<String, String> sendResult = sendResultListenable.get();
        return new ProducedRecord<>(sendResult, failableMessage);
    }
}
