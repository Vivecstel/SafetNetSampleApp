package com.safety.net.sample.events;

public class SafetyNetJobResultEvent {

    private Boolean ctsProfileMatch;
    private String message;

    public SafetyNetJobResultEvent(Boolean ctsProfileMatch, String message) {
        this.ctsProfileMatch = ctsProfileMatch;
        this.message = message;
    }

    public Boolean getCtsProfileMatch() {
        return ctsProfileMatch;
    }

    public void setCtsProfileMatch(Boolean ctsProfileMatch) {
        this.ctsProfileMatch = ctsProfileMatch;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}