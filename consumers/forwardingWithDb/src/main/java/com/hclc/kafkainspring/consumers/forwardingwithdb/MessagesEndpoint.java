package com.hclc.kafkainspring.consumers.forwardingwithdb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/last")
    public Message getLast() {
        return messageRepository.findFirstByOrderByIdDesc();
    }
}
