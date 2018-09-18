package com.hclc.kafkainspring.integrationtests.producer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class Producer {

    private static final String ENDPOINT = "http://localhost:8081";
    private WebTarget producerTarget;

    public Producer() {
        Client client = ClientBuilder.newClient();
        producerTarget = client.target(ENDPOINT);
    }

    public ProducedRecord produce() {
        return producerTarget.path("/produce")
                .queryParam("typeOfFailure", "NONE")
                .request()
                .get()
                .readEntity(ProducedRecord.class);
    }
}
