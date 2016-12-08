package com.safety.net.sample.utils;

import com.safety.net.sample.model.SafetyNetResponse;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// this is the validation class helper
// according to google it is better from your own server to do these validations
public class ValidationHelper {

    // Tag
    private final String TAG = ValidationHelper.this.getClass().getSimpleName();
    // max duration
    private static final long MAX_DURATION = 120000;
    // buffer size
    private static final int BUFFER_SIZE = 2048;
    // Package name
    private String mPackageName;
    // Apk certificate digests
    private List<String> mApkCertificateDigests;
    // Apk digest
    private String mApkDigest;

    public ValidationHelper(Context context) {
        this.mPackageName = context.getPackageName();
        this.mApkCertificateDigests = calculateApkCertificateDigests(context, mPackageName);
        this.mApkDigest = calculateApkDigest(context);
    }

    // validate safety net response
    public boolean validateSafetyNetResponse(SafetyNetResponse response, long timestamp,
                                             byte[] requestNonce) {
        // check if null
        if (response == null) {
            Log.d(TAG, "Safety Net response is null");
            return false;
        //  compare request nonce with response nonce
        } else if (!getRequestNonceBase64(requestNonce).equals(response.getNonce())) {
            Log.d(TAG, "Request nonce : " + getRequestNonceBase64(requestNonce) +
                    " doesn't match with response nonce : " + response.getNonce());
            return false;
        //  compare package name with response package name
        } else if (!mPackageName.equalsIgnoreCase(response.getApkPackageName())) {
            Log.d(TAG, "Package name : " + mPackageName + " doesn't match with response package name : "
                    + response.getApkPackageName());
            return false;
        // compare the total duration of the request with the max duration defined
        } else if (getDurationOfRequest(response, timestamp) > MAX_DURATION) {
            Log.d(TAG, "Duration of the request exceeded the maximum : " + MAX_DURATION + " ms");
            return false;
        // compare the apk certificate digests with the response apk certificate digest
        } else if (!Arrays.equals(mApkCertificateDigests.toArray(), response.getApkCertificateDigestSha256().toArray())) {
            Log.d(TAG, "The apk certificate digests don't match");
            return false;
        // compare the apk digest with the response apk digest
        } else if(!mApkDigest.equals(response.getApkDigestSha256())) {
            Log.d(TAG, "The apk digest : " + mApkDigest + " doesn't match with the response apk digest : " +
                    response.getApkDigestSha256());
            return false;
        // return true if it passes all validations
        } else {
            return true;
        }
    }

    // get request nonce with Base 64 encode
    private String getRequestNonceBase64(byte[] requestNonce) {
        return Base64.encodeToString(requestNonce, 0).trim();
    }

    // get the duration of the request
    private long getDurationOfRequest(SafetyNetResponse response, long timestamp) {
        return response.getTimestampMs() - timestamp;
    }

    // calculate the apk certificate digest
    private List<String> calculateApkCertificateDigests(Context context, String packageName) {
        List<String> encodedSignatureList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());

            return encodedSignatureList;
        }

        Signature[] signatures = packageInfo.signatures;

        for (int i = 0; i < signatures.length; i++) {
            Signature signature = signatures[i];

            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(signature.toByteArray());
                byte[] digest = messageDigest.digest();
                encodedSignatureList.add(Base64.encodeToString(digest, Base64.NO_WRAP));
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return encodedSignatureList;
    }

    // calculate the apk digest
    private String calculateApkDigest(Context context) {
        byte[] apkFileDigest = getApkFileDigest(context);

        return Base64.encodeToString(apkFileDigest, Base64.NO_WRAP);
    }

    // get the apk byte digest
    private byte[] getApkFileDigest(Context context) {
        String apkPath = context.getPackageCodePath();

        try {
            return getDigest(new FileInputStream(apkPath), "SHA-256");
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage());
            return null;
        }
    }

    // get the byte digest
    private byte[] getDigest(InputStream in, String algorithm) throws Throwable {
        MessageDigest md = MessageDigest.getInstance(algorithm);

        try {
            DigestInputStream dis = new DigestInputStream(in, md);
            byte[] buffer = new byte[BUFFER_SIZE];
            while (dis.read(buffer) != -1) { /* empty */}
            dis.close();
            return md.digest();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}