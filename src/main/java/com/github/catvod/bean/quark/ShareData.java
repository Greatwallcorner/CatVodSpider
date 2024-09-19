package com.github.catvod.bean.quark;

public class ShareData {
    private String shareId;
    private String folderId;
    private String sharePwd ;

    public ShareData(String shareId, String folderId) {
        this.shareId = shareId;
        this.folderId = folderId;
    }

    public String getSharePwd() {
        return sharePwd;
    }

    public void setSharePwd(String sharePwd) {
        this.sharePwd = sharePwd;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}
