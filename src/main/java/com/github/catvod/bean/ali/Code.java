package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Code {

    @SerializedName("redirectUri")
    private String redirectUri;

    public static Code objectFrom(String str) {
        return new Gson().fromJson(str, Code.class);
    }

    public String getRedirectUri() {
        return StringUtils.isEmpty(redirectUri) ? "" : redirectUri;
    }

    public String getCode() {
        return getRedirectUri().split("code=")[1];
    }
}
