package com.safety.net.sample.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyResponse {

    @Expose
    @SerializedName("isValidSignature")
    private  Boolean isValidSignature;

    public VerifyResponse() {
    }

    public VerifyResponse(Boolean isValidSignature) {
        this.isValidSignature = isValidSignature;
    }

    public Boolean getIsValidSignature() {
        return isValidSignature;
    }

    public void setIsValidSignature(Boolean validSignature) {
        isValidSignature = validSignature;
    }

    @Override
    public String toString() {
        return "VerifyResponse{" +
                "isValidSignature=" + isValidSignature +
                '}';
    }
}