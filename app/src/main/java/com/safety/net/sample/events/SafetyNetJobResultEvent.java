package com.safety.net.sample.events;

public class SafetyNetJobResultEvent {

    private String errorMessage;

    public SafetyNetJobResultEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}