package com.hclc.kafkainspring.integrationtests;

public enum TypeOfFailure {
    EXCEPTION_AFTER_CONSUMED, EXCEPTION_AFTER_FORWARDED, PROCESSING_TIME_EXCEEDING_SESSION_TIMEOUT, NONE
}
