package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class Share {

    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    @SerializedName("display_message")
    private String displayMessage;

    @SerializedName("share_id")
    private String shareId;
    @SerializedName("share_token")
    private String shareToken;
    @SerializedName("expire_time")
    private String expireTime;
    @SerializedName("expires_in")
    private int expiresIn;

    @SerializedName("creator_id")
    private String creatorId;
    @SerializedName("creator_name")
    private String creatorName;
    @SerializedName("creator_phone")
    private String creatorPhone;
    @SerializedName("expiration")
    private String expiration;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("vip")
    private String vip;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("share_name")
    private String shareName;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("share_title")
    private String shareTitle;
    @SerializedName("has_pwd")
    private boolean hasPwd;
    @SerializedName("file_infos")
    private List<Item> fileInfos;

    private long time;

    public static Share objectFrom(String str) {
        return new Gson().fromJson(str, Share.class);
    }

    public String getShareId() {
        return StringUtils.isEmpty(shareId) ? "" : shareId;
    }

    public String getShareToken() {
        return StringUtils.isEmpty(shareToken) ? "" : shareToken;
    }

    public String getExpireTime() {
        return StringUtils.isEmpty(expireTime) ? "" : expireTime;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getCreatorId() {
        return StringUtils.isEmpty(creatorId) ? "" : creatorId;
    }

    public String getCreatorName() {
        return StringUtils.isEmpty(creatorName) ? "" : creatorName;
    }

    public String getCreatorPhone() {
        return StringUtils.isEmpty(creatorPhone) ? "" : creatorPhone;
    }

    public String getExpiration() {
        return StringUtils.isEmpty(expiration) ? "" : expiration;
    }

    public String getUpdatedAt() {
        return StringUtils.isEmpty(updatedAt) ? "" : updatedAt;
    }

    public String getVip() {
        return StringUtils.isEmpty(vip) ? "" : vip;
    }

    public String getAvatar() {
        return StringUtils.isEmpty(avatar) ? "" : avatar;
    }

    public String getShareName() {
        return StringUtils.isEmpty(shareName) ? "" : shareName;
    }

    public String getDisplayName() {
        return StringUtils.isEmpty(displayName) ? "" : displayName;
    }

    public String getShareTitle() {
        return StringUtils.isEmpty(shareTitle) ? "" : shareTitle;
    }

    public boolean isHasPwd() {
        return hasPwd;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public List<Item> getFileInfos() {
        return fileInfos == null ? Collections.emptyList() : fileInfos;
    }

    public Share setTime() {
        this.time = System.currentTimeMillis() + 60 * 60 * 1000;
        return this;
    }

    public Share setShareId(String shareId) {
        this.shareId = shareId;
        return this;
    }

    public boolean alive(String shareId) {
        return getShareId().equals(shareId) && System.currentTimeMillis() <= time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
