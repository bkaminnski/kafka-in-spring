package com.hclc.kafkainspring.consumers.forwardingwithdb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("messages")
public class MessagesEndpoint {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public Iterable<Message> getAll() {
        return messageRepository.findAll();
    }

    @DeleteMapping
    @Transactional("jpaTransactionManager")
    public void deleteAll() {
        messageRepository.deleteAll();
    }

    @PostMapping
    @Transactional("jpaTransactionManager")
    public Message create(@RequestBody Message message) {
        return messageRepository.save(message);
    }

    @GetMapping("/last")
    public Message getLast() {
        return messageRepository.findFirstByOrderByIdDesc();
    }
}
