package com.github.catvod.bean.alist;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Param {

    @SerializedName("path")
    private String path;
    @SerializedName("pass")
    private String pass;

    public String getPath() {
        return StringUtils.isEmpty(path) ? "" : path;
    }

    public String getPass() {
        return StringUtils.isEmpty(pass) ? "" : pass;
    }
}
