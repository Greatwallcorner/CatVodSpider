package com.github.catvod.bean.alist;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Login {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public String getUsername() {
        return StringUtils.isEmpty(username) ? "" : username;
    }

    public String getPassword() {
        return StringUtils.isEmpty(password) ? "" : password;
    }
}
