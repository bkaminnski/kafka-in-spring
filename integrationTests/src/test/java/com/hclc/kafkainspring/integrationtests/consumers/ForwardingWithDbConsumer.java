package com.hclc.kafkainspring.integrationtests.consumers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class ForwardingWithDbConsumer extends Consumer {

    private static final String ENDPOINT = "http://localhost:8085";
    private static final String MESSAGES = "/messages";
    private static final String LAST_MESSAGE = MESSAGES + "/last";

    public ForwardingWithDbConsumer() {
        super(ENDPOINT);
    }

    @Override
    public void drain() {
        super.drain();
        deleteAllMessages();
    }

    private Response deleteAllMessages() {
        return consumerTarget.path(MESSAGES)
                .request()
                .delete();
    }

    public Message getLastMessage() {
        return consumerTarget.path(LAST_MESSAGE)
                .request()
                .get()
                .readEntity(Message.class);
    }

    public Message generateReferenceMessage() {
        return consumerTarget.path(MESSAGES)
                .request()
                .post(Entity.json(new Message("Reference message " + UUID.randomUUID().toString())))
                .readEntity(Message.class);
    }
}
