package com.github.catvod.bean.quark;

import com.github.catvod.utils.Util;

import java.util.Map;
import java.util.regex.Pattern;

public class Item {
    private String fileId;
    private String shareId;
    private String shareToken;
    private String shareFileToken;
    private String seriesId;
    private String name;
    private String type;
    private String formatType;
    private Double size;
    private String parent;
    private String shareData;
    private int shareIndex;
    private Double lastUpdateAt;
    private String subtitle;

    public Item() {
        this.fileId = "";
        this.shareId = "";
        this.shareToken = "";
        this.shareFileToken = "";
        this.seriesId = "";
        this.name = "";
        this.type = "";
        this.formatType = "";
        this.size = 0d;
        this.parent = "";
        this.shareData = null;
        this.shareIndex = 0;
        this.lastUpdateAt = 0d;
    }

    public static Item objectFrom(Map<String, Object> item_json, String shareId, int shareIndex) {
        Item item = new Item();
        item.fileId = item_json.get("fid") != null ? (String) item_json.get("fid") : "";
        item.shareId = shareId;
        item.shareToken = item_json.get("stoken") != null ? (String) item_json.get("stoken") : "";
        item.shareFileToken = item_json.get("share_fid_token") != null ? (String) item_json.get("share_fid_token") : "";
        item.seriesId = item_json.get("series_id") != null ? (String) item_json.get("series_id") : "";
        item.name = item_json.get("file_name") != null ? (String) item_json.get("file_name") : "";
        item.type = item_json.get("obj_category") != null ? (String) item_json.get("obj_category") : "";
        item.formatType = item_json.get("format_type") != null ? (String) item_json.get("format_type") : "";
        item.size = item_json.get("size") != null ? (Double) item_json.get("size") : 0d;
        item.parent = item_json.get("pdir_fid") != null ? (String) item_json.get("pdir_fid") : "";
        item.lastUpdateAt = item_json.get("last_update_at") != null ? (Double) item_json.get("last_update_at") : Double.valueOf(0d);
        item.shareIndex = shareIndex;
        return item;
    }

    public String getFileExtension() {
        String[] arr = name.split("\\.");
        return arr[arr.length - 1];
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getFileId() {
        return fileId.isEmpty() ? "" : fileId;
    }

    public String getName() {
        return name.isEmpty() ? "" : name;
    }

    public String getParent() {
        return parent.isEmpty() ? "" : "[" + parent + "]";
    }

    public String getSize() {
        return size.equals("0") ? "" : "[" + size + "]";
    }


    public int getShareIndex() {
        return shareIndex;
    }

    public String getDisplayName(String type_name) {
        String name = getName();
        if (type_name.equals("电视剧")) {
            String[] replaceNameList = {"4k", "4K"};
            name = name.replaceAll("\\." + getFileExtension(), "");
            for (String replaceName : replaceNameList) {
                name = name.replaceAll(replaceName, "");
            }
            name = Pattern.compile("/\\.S01E(.*?)\\./").matcher(name).find() ? name.split("/\\.S01E(.*?)\\./")[1] : name;
            String[] numbers = name.split("\\d+");
            if (numbers.length > 0) {
                name = numbers[0];
            }
        }
        return name + " " + Util.getSize(size);
    }

    public String getEpisodeUrl(String type_name) {
        return getDisplayName(type_name) + "$" + getFileId() + "++" + shareFileToken + "++" + shareId + "++" + shareToken;
    }
}

