package com.github.catvod.bean.upyun;

import com.github.catvod.bean.Vod;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Item {

    @SerializedName("title")
    private String title;
    @SerializedName("page_url")
    private String pageUrl;
    @SerializedName("insert_time")
    private String insertTime;

    public String getTitle() {
        return StringUtils.isEmpty(title) ? "" : title.replaceAll("<em>", "").replaceAll("</em>", "");
    }

    public String getPageUrl() {
        return StringUtils.isEmpty(pageUrl) ? "" : pageUrl;
    }

    public String getInsertTime() {
        return StringUtils.isEmpty(insertTime) ? "" : insertTime;
    }

    public Item url(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public Vod getVod() {
        return new Vod(getPageUrl(), getTitle(), "https://inews.gtimg.com/newsapp_bt/0/13263837859/1000", getInsertTime());
    }
}
