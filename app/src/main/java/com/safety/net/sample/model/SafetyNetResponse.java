package com.safety.net.sample.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SafetyNetResponse {

    @Expose
    @SerializedName("nonce")
    private String nonce;

    @Expose
    @SerializedName("timestampMs")
    private Long timestampMs;

    @Expose
    @SerializedName("apkPackageName")
    private String apkPackageName;

    @Expose
    @SerializedName("apkDigestSha256")
    private String apkDigestSha256;

    @Expose
    @SerializedName("ctsProfileMatch")
    private Boolean ctsProfileMatch;

    @Expose
    @SerializedName("extension")
    private String extension;

    @Expose
    @SerializedName("apkCertificateDigestSha256")
    private List<String> apkCertificateDigestSha256 = null;

    @Expose
    @SerializedName("basicIntegrity")
    private Boolean basicIntegrity;

    public SafetyNetResponse() {
    }

    public SafetyNetResponse(String nonce, Long timestampMs, String apkPackageName,
                             String apkDigestSha256, Boolean ctsProfileMatch, String extension,
                             List<String> apkCertificateDigestSha256, Boolean basicIntegrity) {
        this.nonce = nonce;
        this.timestampMs = timestampMs;
        this.apkPackageName = apkPackageName;
        this.apkDigestSha256 = apkDigestSha256;
        this.ctsProfileMatch = ctsProfileMatch;
        this.extension = extension;
        this.apkCertificateDigestSha256 = apkCertificateDigestSha256;
        this.basicIntegrity = basicIntegrity;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(Long timestampMs) {
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