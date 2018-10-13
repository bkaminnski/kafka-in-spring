package com.hclc.kafkainspring.integrationtests.consumers;

public class ConsumedRecordResponse extends Response {

    private final ConsumptionState consumptionState;

    public ConsumedRecordResponse(int statusCode, ConsumptionState consumptionState) {
        super(statusCode);
        this.consumptionState = consumptionState;
    }

    public ConsumptionState getConsumptionState() {
        return consumptionState;
    }
}
