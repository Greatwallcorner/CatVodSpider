package com.github.catvod.bean.bili;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class Data {

    @SerializedName("result")
    private JsonElement result;
    @SerializedName("list")
    private JsonElement list;
    @SerializedName("isLogin")
    private Boolean isLogin;
    @SerializedName("vipStatus")
    private Integer vipStatus;
    @SerializedName("qrcode_key")
    private String qrcodeKey;
    @SerializedName("url")
    private String url;
    @SerializedName("aid")
    private String aid;
    @SerializedName("cid")
    private String cid;
    @SerializedName("title")
    private String title;
    @SerializedName("tname")
    private String tname;
    @SerializedName("pic")
    private String pic;
    @SerializedName("duration")
    private Long duration;
    @SerializedName("desc")
    private String desc;
    @SerializedName("accept_description")
    private List<String> acceptDescription;
    @SerializedName("accept_quality")
    private List<Integer> acceptQuality;
    @SerializedName("pages")
    private List<Page> pages;
    @SerializedName("dash")
    private Dash dash;
    @SerializedName("owner")
    private Owner owner;
    @SerializedName("wbi_img")
    private Wbi wbi;

    private Durl[] durl;

    public Durl[] getDurl() {
        return durl;
    }

//    public void setDurl(Durl[] durl) {
//        this.durl = durl;
//    }

    public JsonElement getResult() {
        return result;
    }

    public JsonElement getList() {
        return list;
    }

    public boolean isLogin() {
        return isLogin != null && isLogin;
    }

    public Integer getVipStatus() {
        return vipStatus;
    }

    public boolean isVip() {
        return vipStatus != null && vipStatus != 0;
    }

    public String getQrcodeKey() {
        return StringUtils.isEmpty(qrcodeKey) ? "" : qrcodeKey;
    }

    public String getUrl() {
        return StringUtils.isEmpty(url) ? "" : url;
    }

    public String getAid() {
        return StringUtils.isEmpty(aid) ? "" : aid;
    }

    public String getCid() {
        return StringUtils.isEmpty(cid) ? "" : cid;
    }

    public String getTitle() {
        return StringUtils.isEmpty(title) ? "" : title;
    }

    public String getType() {
        return StringUtils.isEmpty(tname) ? "" : tname;
    }

    public String getPic() {
        return StringUtils.isEmpty(pic) ? "" : pic;
    }

    public Long getDuration() {
        return duration == null ? 0 : duration;
    }

    public String getDesc() {
        return StringUtils.isEmpty(desc) ? "" : desc;
    }

    public List<String> getAcceptDescription() {
        return acceptDescription == null ? Collections.emptyList() : acceptDescription;
    }

    public List<Integer> getAcceptQuality() {
        return acceptQuality == null ? Collections.emptyList() : acceptQuality;
    }

    public List<Page> getPages() {
        return pages == null ? Collections.emptyList() : pages;
    }

    public Dash getDash() {
        return dash == null ? new Dash() : dash;
    }

    public Owner getOwner() {
        return owner == null ? new Owner() : owner;
    }

    public Wbi getWbi() {
        return wbi == null ? new Wbi() : wbi;
    }
}
