package com.safety.net.sample.model;

public class VerifyRequest {

    private String signedAttestation;

    public VerifyRequest() {
    }

    public VerifyRequest(String signedAttestation) {
        this.signedAttestation = signedAttestation;
    }

    public String getSignedAttestation() {
        return signedAttestation;
    }

    public void setSignedAttestation(String signedAttestation) {
        this.signedAttestation = signedAttestation;
    }

    @Override
    public String toString() {
        return "VerifyRequest{" +
                "signedAttestation='" + signedAttestation + '\'' +
                '}';
    }
}