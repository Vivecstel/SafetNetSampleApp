package com.safety.net.sample.events;

import java.util.Arrays;

public class SafetyNetEvent {

    private String jwsResult;

    private long timestamp;

    private byte[] requestNonce;

    public SafetyNetEvent(String jwsResult, long timestamp, byte[] requestNonce) {
        this.jwsResult = jwsResult;
        this.timestamp = timestamp;
        this.requestNonce = requestNonce;
    }

    public String getJwsResult() {
        return jwsResult;
    }

    public void setJwsResult(String jwsResult) {
        this.jwsResult = jwsResult;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getRequestNonce() {
        return requestNonce;
    }

    public void setRequestNonce(byte[] requestNonce) {
        this.requestNonce = requestNonce;
    }

    @Override
    public String toString() {
        return "SafetyNetEvent{" +
                "jwsResult='" + jwsResult + '\'' +
                ", timestamp=" + timestamp +
                ", requestNonce=" + Arrays.toString(requestNonce) +
                '}';
    }
}