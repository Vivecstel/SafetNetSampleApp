package com.safety.net.sample.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultModel implements Parcelable {

    private String username;
    private Boolean ctsProfileMatch;
    private String message;

    public ResultModel() {
    }

    public ResultModel(String username, Boolean ctsProfileMatch, String message) {
        this.username = username;
        this.ctsProfileMatch = ctsProfileMatch;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "ResultModel{" +
                "username='" + username + '\'' +
                ", ctsProfileMatch=" + ctsProfileMatch +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeValue(this.ctsProfileMatch);
        dest.writeString(this.message);
    }

    protected ResultModel(Parcel in) {
        this.username = in.readString();
        this.ctsProfileMatch = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
    }

    public static final Parcelable.Creator<ResultModel> CREATOR = new Parcelable.Creator<ResultModel>() {
        @Override
        public ResultModel createFromParcel(Parcel source) {
            return new ResultModel(source);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };
}