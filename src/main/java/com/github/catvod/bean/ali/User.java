package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class User {

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;

    public static User objectFrom(String str) {
        User item = new Gson().fromJson(str, User.class);
        return item == null ? new User() : item;
    }

    public String getTokenType() {
        return StringUtils.isEmpty(tokenType) ? "" : tokenType;
    }

    public String getAccessToken() {
        return StringUtils.isEmpty(accessToken) ? "" : accessToken;
    }

    public String getRefreshToken() {
        return StringUtils.isEmpty(refreshToken) ? "" : refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAuthorization() {
        return getTokenType() + " " + getAccessToken();
    }

    public boolean isAuthed() {
        return getTokenType().length() > 0 && getAccessToken().length() > 0;
    }

    public void clean() {
        this.refreshToken = "";
        this.accessToken = "";
    }
}
