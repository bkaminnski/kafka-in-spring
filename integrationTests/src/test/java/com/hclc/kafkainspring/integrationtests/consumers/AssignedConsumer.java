package com.hclc.kafkainspring.integrationtests.consumerassign;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.glassfish.grizzly.http.util.HttpStatus.GATEWAY_TIMEOUT_504;

public class ConsumerAssign {

    private static final String ENDPOINT = "http://localhost:8087";
    private WebTarget consumerAssignTarget;

    public ConsumerAssign() {
        Client client = ClientBuilder.newClient();
        consumerAssignTarget = client.target(ENDPOINT);
    }

    public ConsumedRecord readConsumed() {
        return readConsumedWithTimeoutMillis(1000).readEntity(ConsumedRecord.class);
    }

    public void drain() {
        while (readConsumedWithTimeoutMillis(0).getStatus() != GATEWAY_TIMEOUT_504.getStatusCode()) {
        }
    }

    private Response readConsumedWithTimeoutMillis(int timeoutMillis) {
        return consumerAssignTarget.path("/consumed")
                .queryParam("timeoutMillis", timeoutMillis)
                .request()
                .get();
    }
}
