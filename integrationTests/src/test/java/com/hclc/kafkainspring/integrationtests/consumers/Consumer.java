package com.hclc.kafkainspring.integrationtests.consumers;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.glassfish.grizzly.http.util.HttpStatus.GATEWAY_TIMEOUT_504;

public abstract class Consumer {
    private static final String CONSUMED = "/consumed";
    private static final String ERROR_HANDLED = "/errorHandled";
    private WebTarget assignedConsumerTarget;

    Consumer(String endpoint) {
        Client client = ClientBuilder.newClient();
        assignedConsumerTarget = client.target(endpoint);
    }

    public ConsumedRecordResponse readConsumed() {
        Response response = readWithTimeoutMillis(CONSUMED, 300);
        return new ConsumedRecordResponse(response.getStatus(), response.readEntity(ConsumptionState.class));
    }

    public ErrorHandledRecordResponse readErrorHandled() {
        Response response = readWithTimeoutMillis(ERROR_HANDLED, 300);
        return new ErrorHandledRecordResponse(response.getStatus(), response.readEntity(ErrorHandlingState.class));
    }

    private Response readWithTimeoutMillis(String endpoint, int timeoutMillis) {
        return assignedConsumerTarget.path(endpoint)
                .queryParam("timeoutMillis", timeoutMillis)
                .request()
                .get();
    }

    public void drain() {
        drainConsumed();
        drainErrorHandled();
    }

    private void drainConsumed() {
        while (readWithTimeoutMillis(CONSUMED, 0).getStatus() != GATEWAY_TIMEOUT_504.getStatusCode()) {
        }
    }

    private void drainErrorHandled() {
        while (readWithTimeoutMillis(ERROR_HANDLED, 0).getStatus() != GATEWAY_TIMEOUT_504.getStatusCode()) {
        }
    }
}
