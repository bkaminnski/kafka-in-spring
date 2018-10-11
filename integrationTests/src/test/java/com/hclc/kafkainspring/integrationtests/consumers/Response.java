package com.hclc.kafkainspring.integrationtests.consumers;

import static org.glassfish.grizzly.http.util.HttpStatus.GATEWAY_TIMEOUT_504;
import static org.glassfish.grizzly.http.util.HttpStatus.OK_200;

public class Response {

    private final int statusCode;

    public Response(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isOk() {
        return statusCode == OK_200.getStatusCode();
    }

    public boolean isTimedOut() {
        return statusCode == GATEWAY_TIMEOUT_504.getStatusCode();
    }
}
