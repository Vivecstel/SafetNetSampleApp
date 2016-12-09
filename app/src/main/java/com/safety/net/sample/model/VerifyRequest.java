package com.safety.net.sample.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyRequest {

    @Expose
    @SerializedName("signedAttestation")
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