package com.safety.net.sample.events;

public class AndroidVerificationJobResultEvent {

    private Boolean ctsProfileMatch;
    private String message;

    public AndroidVerificationJobResultEvent(Boolean ctsProfileMatch, String message) {
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