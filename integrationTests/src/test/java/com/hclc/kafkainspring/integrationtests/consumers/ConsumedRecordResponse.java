package com.hclc.kafkainspring.integrationtests.consumers;

public class ConsumedRecordResponse extends Response {

    private final ConsumedRecord consumedRecord;

    public ConsumedRecordResponse(int statusCode, ConsumedRecord consumedRecord) {
        super(statusCode);
        this.consumedRecord = consumedRecord;
    }

    public ConsumedRecord getConsumedRecord() {
        return consumedRecord;
    }

}
