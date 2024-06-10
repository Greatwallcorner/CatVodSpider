package com.github.catvod.bean.ali;

import com.google.gson.Gson;

public class StatusData {
    private String status;

    private String authCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public static StatusData objectFrom(String str) {
        try {
            StatusData data = new Gson().fromJson(str, StatusData.class);
            return data == null ? new StatusData() : data;
        } catch (Exception e) {
            return new StatusData();
        }
    }

}
