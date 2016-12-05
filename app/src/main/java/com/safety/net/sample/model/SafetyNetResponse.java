package com.safety.net.sample.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SafetyNetResponse {

    @SerializedName("nonce")
    private String nonce;

    @SerializedName("timestampMs")
    private Integer timestampMs;

    @SerializedName("apkPackageName")
    private String apkPackageName;

    @SerializedName("apkDigestSha256")
    private String apkDigestSha256;

    @SerializedName("ctsProfileMatch")
    private Boolean ctsProfileMatch;

    @SerializedName("extension")
    private String extension;

    @SerializedName("apkCertificateDigestSha256")
    private List<String> apkCertificateDigestSha256 = null;

    @SerializedName("basicIntegrity")
    private Boolean basicIntegrity;

    public SafetyNetResponse() {
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Integer getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(Integer timestampMs) {
        this.timestampMs = timestampMs;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public String getApkDigestSha256() {
        return apkDigestSha256;
    }

    public void setApkDigestSha256(String apkDigestSha256) {
        this.apkDigestSha256 = apkDigestSha256;
    }

    public Boolean getCtsProfileMatch() {
        return ctsProfileMatch;
    }

    public void setCtsProfileMatch(Boolean ctsProfileMatch) {
        this.ctsProfileMatch = ctsProfileMatch;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<String> getApkCertificateDigestSha256() {
        return apkCertificateDigestSha256;
    }

    public void setApkCertificateDigestSha256(List<String> apkCertificateDigestSha256) {
        this.apkCertificateDigestSha256 = apkCertificateDigestSha256;
    }

    public Boolean getBasicIntegrity() {
        return basicIntegrity;
    }

    public void setBasicIntegrity(Boolean basicIntegrity) {
        this.basicIntegrity = basicIntegrity;
    }

    @Override
    public String toString() {
        return "SafetyNetResponse{" +
                "nonce='" + nonce + '\'' +
                ", timestampMs=" + timestampMs +
                ", apkPackageName='" + apkPackageName + '\'' +
                ", apkDigestSha256='" + apkDigestSha256 + '\'' +
                ", ctsProfileMatch=" + ctsProfileMatch +
                ", extension='" + extension + '\'' +
                ", apkCertificateDigestSha256=" + apkCertificateDigestSha256 +
                ", basicIntegrity=" + basicIntegrity +
                '}';
    }
}