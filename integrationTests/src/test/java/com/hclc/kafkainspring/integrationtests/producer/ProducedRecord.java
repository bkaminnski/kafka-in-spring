package com.hclc.kafkainspring.integrationtests.producer;

import com.hclc.kafkainspring.integrationtests.FailableMessage;

public class ProducedRecord {

    private SendResult<String, String> sendResult;
    private FailableMessage failableMessage;

    public SendResult<String, String> getSendResult() {
        return sendResult;
    }

    public void setSendResult(SendResult<String, String> sendResult) {
        this.sendResult = sendResult;
    }

    public FailableMessage getFailableMessage() {
        return failableMessage;
    }

    public void setFailableMessage(FailableMessage failableMessage) {
        this.failableMessage = failableMessage;
    }
}
