package com.hclc.kafkainspring.kafkalisteners;

public class TransitionRequest {

    private NextState nextState;

    public NextState getNextState() {
        return nextState;
    }

    public void setNextState(NextState nextState) {
        this.nextState = nextState;
    }
}
