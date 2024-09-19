package com.github.catvod.bean.quark;

import com.google.gson.annotations.SerializedName;

public class User {
    public User(String cookie) {
        this.cookie = cookie;
    }

    @SerializedName("cookie")
    private String cookie;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public static User objectFrom(String cookie) {
        return new User(cookie);
    }


    public void clean() {
        this.cookie = "";

    }
}
