package com.hclc.kafkainspring.integrationtests.producer;

import com.hclc.kafkainspring.integrationtests.TypeOfFailure;

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

    public ProducedRecord produce(String toTopic, TypeOfFailure typeOfFailure, int failuresCount) {
        return producerTarget.path("/produce")
                .queryParam("toTopic", toTopic)
                .queryParam("typeOfFailure", typeOfFailure.toString())
                .queryParam("failuresCount", failuresCount)
                .request()
                .get()
                .readEntity(ProducedRecord.class);
    }
}
