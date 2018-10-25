package com.hclc.kafkainspring.kafkalisteners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RestController
public class KafkaListenersEndpoint {

    @Autowired(required = false)
    private List<KafkaMessageListenerContainer<?, ?>> listenerContainers = new ArrayList<>();
    private Map<String, KafkaMessageListenerContainer<?, ?>> listenerContainersByName;

    @PostConstruct
    private void fillInListenerContainersByName() {
        listenerContainersByName = listenerContainers.stream().collect(toMap(KafkaMessageListenerContainer::getBeanName, c -> c));
    }

    @GetMapping("/kafkaListeners")
    public ResponseEntity<List<KafkaListener>> getAll() {
        return ResponseEntity.ok(listenerContainers.stream().map(KafkaListener::new).collect(toList()));
    }

    @GetMapping("/kafkaListeners/{name}")
    public ResponseEntity<KafkaListener> getByName(@PathVariable("name") String name) {
        return Optional.ofNullable(listenerContainersByName.get(name))
                .map(KafkaListener::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/kafkaListeners/{name}/transitions")
    public ResponseEntity<KafkaListener> newTransition(@PathVariable("name") String name, @RequestBody TransitionRequest transitionRequest) {
        NextState nextState = transitionRequest.getNextState();
        return Optional.ofNullable(listenerContainersByName.get(name))
                .map(nextState::perform)
                .map(KafkaListener::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
