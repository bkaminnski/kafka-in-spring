package com.hclc.kafkainspring.integrationtests.consumers;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static javax.ws.rs.client.Entity.json;
import static org.glassfish.grizzly.http.util.HttpStatus.GATEWAY_TIMEOUT_504;
import static org.glassfish.grizzly.http.util.HttpStatus.OK_200;

public abstract class Consumer {
    private static final String CONSUMED = "/consumed";
    private static final String ERROR_HANDLED = "/errorHandled";
    private static final String LOGGED = "/logged";
    private static final String KAFKA_LISTENER = "/kafkaListeners/%s";
    private static final String KAFKA_LISTENER_TRANSITIONS = KAFKA_LISTENER + "/transitions";
    private WebTarget consumerTarget;

    Consumer(String endpoint) {
        Client client = ClientBuilder.newClient();
        consumerTarget = client.target(endpoint);
    }

    public ConsumedRecordResponse readConsumed(long additionalIntervalMillisForPolling) {
        Response response = readWithTimeoutMillis(CONSUMED, 300 + additionalIntervalMillisForPolling);
        return new ConsumedRecordResponse(response.getStatus(), response.readEntity(ConsumptionState.class));
    }

    public ErrorHandledRecordResponse readErrorHandled(long additionalIntervalMillisForPolling) {
        Response response = readWithTimeoutMillis(ERROR_HANDLED, 300 + additionalIntervalMillisForPolling);
        return new ErrorHandledRecordResponse(response.getStatus(), response.readEntity(ErrorHandlingState.class));
    }

    private Response readWithTimeoutMillis(String endpoint, long timeoutMillis) {
        return consumerTarget.path(endpoint)
                .queryParam("timeoutMillis", timeoutMillis)
                .request()
                .get();
    }

    public void drain() {
        drainConsumed();
        drainErrorHandled();
        drainLogged();
    }

    private void drainConsumed() {
        while (readWithTimeoutMillis(CONSUMED, 0).getStatus() != GATEWAY_TIMEOUT_504.getStatusCode()) {
        }
    }

    private void drainErrorHandled() {
        while (readWithTimeoutMillis(ERROR_HANDLED, 0).getStatus() != GATEWAY_TIMEOUT_504.getStatusCode()) {
        }
    }

    private void drainLogged() {
        while (readWithTimeoutMillis(LOGGED, 0).getStatus() != GATEWAY_TIMEOUT_504.getStatusCode()) {
        }
    }

    public int resumeConsumptionOn(String listenerContainer) {
        JsonObject transitionRequest = Json.createObjectBuilder()
                .add("nextState", "RUNNING")
                .build();
        return consumerTarget.path(format(KAFKA_LISTENER_TRANSITIONS, listenerContainer))
                .request()
                .post(json(transitionRequest))
                .getStatus();
    }

    public boolean waitUntilListenerIsRunning(String listenerContainer) {
        boolean containerIsPaused = true;
        for (int i = 0; containerIsPaused && i < 10; i++) {
            wait100millis();
            containerIsPaused = consumerTarget.path(format(KAFKA_LISTENER, listenerContainer))
                    .request()
                    .get()
                    .readEntity(JsonObject.class)
                    .getBoolean("containerPaused");
        }
        return !containerIsPaused;
    }

    public boolean waitUntilLogged(String message, int consumerIndex) {
        boolean conditionsAreMet = false;
        for (int i = 0; !conditionsAreMet && i < 100; i++) {
            wait100millis();
            Response response = consumerTarget.path(LOGGED)
                    .queryParam("timeoutMillis", 100)
                    .request()
                    .get();
            if (response.getStatus() == OK_200.getStatusCode()) {
                LoggedRecord loggedRecord = response
                        .readEntity(LoggingState.class)
                        .getHeadOfQueue();
                conditionsAreMet = loggedRecord.isForConsumerIndex(consumerIndex) && message.equals(loggedRecord.getMessage());
            }
        }
        return conditionsAreMet;
    }

    private void wait100millis() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }
}
