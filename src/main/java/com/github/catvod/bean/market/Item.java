package com.github.catvod.bean.market;

import com.github.catvod.bean.Vod;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Item {

    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("icon")
    private String icon;
    @SerializedName("copy")
    private String copy;
    @SerializedName("version")
    private String version;

    public Item(String url) {
        this.url = url;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "" : name;
    }

    public String getUrl() {
        return StringUtils.isEmpty(url) ? "" : url;
    }

    public String getIcon() {
        return StringUtils.isEmpty(icon) ? "" : icon;
    }

    public String getCopy() {
        return StringUtils.isEmpty(copy) ? "" : copy;
    }

    public String getVersion() {
        return StringUtils.isEmpty(version) ? "" : version;
    }

    public Vod vod() {
        return new Vod(getUrl(), getName(), getIcon(), getVersion(), Vod.Style.rect(1.0f));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Item)) return false;
        Item it = (Item) obj;
        return getUrl().equals(it.getUrl());
    }
}
