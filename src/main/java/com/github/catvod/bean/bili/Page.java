package com.github.catvod.bean.bili;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Page {

    @SerializedName("cid")
    private String cid;
    @SerializedName("part")
    private String part;

    public String getCid() {
        return StringUtils.isEmpty(cid) ? "" : cid;
    }

    public String getPart() {
        return StringUtils.isEmpty(part) ? "" : part;
    }
}
