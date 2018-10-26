package com.hclc.kafkainspring.consumers.forwardingwithdb;

import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {

    Message findFirstByOrderByIdDesc();
}
