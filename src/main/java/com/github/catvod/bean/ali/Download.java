package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Download {

    @SerializedName("url")
    private String url;
    @SerializedName("file_id")
    private String fileId;
    @SerializedName("expiration")
    private String expiration;

    public static Download objectFrom(String str) {
        return new Gson().fromJson(str, Download.class);
    }

    public String getUrl() {
        return StringUtils.isEmpty(url) ? "" : url;
    }

    public String getFileId() {
        return StringUtils.isEmpty(fileId) ? "" : fileId;
    }

    public String getExpiration() {
        return StringUtils.isEmpty(expiration) ? "" : expiration;
    }
}
