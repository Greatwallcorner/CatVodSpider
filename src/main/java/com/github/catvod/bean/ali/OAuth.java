package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class OAuth {

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;

    public static OAuth objectFrom(String str) {
        OAuth item = new Gson().fromJson(str, OAuth.class);
        return item == null ? new OAuth() : item;
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

    public String getAuthorization() {
        return getTokenType() + " " + getAccessToken();
    }

    public void clean() {
        this.refreshToken = "";
        this.accessToken = "";
    }
}
