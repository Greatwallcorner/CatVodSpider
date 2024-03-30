package com.github.catvod.bean.bili;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Owner {

    @SerializedName("mid")
    private String mid;
    @SerializedName("name")
    private String name;

    public String getMid() {
        return StringUtils.isEmpty(mid) ? "" : mid;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "" : name;
    }

    public String getFormat() {
        return String.format("[a=cr:{\"id\":\"%s\",\"name\":\"%s\"}/]%s[/a]", getMid() + "/{pg}", getName(), getName());
    }
}
