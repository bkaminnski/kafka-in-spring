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
        return produce(toTopic, typeOfFailure, failuresCount, null);
    }

    public ProducedRecord produce(String toTopic, TypeOfFailure typeOfFailure, int failuresCount, Integer partition) {
        return producerTarget.path("/produce")
                .queryParam("toTopic", toTopic)
                .queryParam("typeOfFailure", typeOfFailure.toString())
                .queryParam("failuresCount", failuresCount)
                .queryParam("partition", partition)
                .request()
                .get()
                .readEntity(ProducedRecord.class);
    }
}
