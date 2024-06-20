package com.github.catvod.bean.bili;

import com.google.gson.annotations.SerializedName;

public class Durl {
    private Integer order;

    private Integer length;

    private Integer size;

    private String ahead;

    private String vhead;

    private String url;

    @SerializedName("backup_url")
    private String[] backUpUrl;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getAhead() {
        return ahead;
    }

    public void setAhead(String ahead) {
        this.ahead = ahead;
    }

    public String getVhead() {
        return vhead;
    }

    public void setVhead(String vhead) {
        this.vhead = vhead;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getBackUpUrl() {
        return backUpUrl;
    }

    public void setBackUpUrl(String[] backUpUrl) {
        this.backUpUrl = backUpUrl;
    }
}
