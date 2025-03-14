package com.github.catvod.bean.ali;

import com.github.catvod.utils.Util;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Item implements Comparable<Item> {

    @SerializedName("items")
    private List<Item> items;
    @SerializedName("next_marker")
    private String nextMarker;
    @SerializedName("file_id")
    private String fileId;
    @SerializedName("share_id")
    private String shareId;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("file_extension")
    private String fileExtension;
    @SerializedName("category")
    private String category;
    @SerializedName("size")
    private double size;
    @SerializedName("parent")
    private String parent;

    public static Item objectFrom(String str) {
        return new Gson().fromJson(str, Item.class);
    }

    public Item(String fileId) {
        this.fileId = fileId;
    }

    public List<Item> getItems() {
        return items == null ? Collections.emptyList() : items;
    }

    public String getNextMarker() {
        return StringUtils.isEmpty(nextMarker) ? "" : nextMarker;
    }

    public String getFileId() {
        return StringUtils.isEmpty(fileId) ? "" : fileId;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "" : name;
    }

    public String getType() {
        return StringUtils.isEmpty(type) ? "" : type;
    }

    public String getExt() {
        return StringUtils.isEmpty(fileExtension) ? "" : fileExtension;
    }

    public String getCategory() {
        return StringUtils.isEmpty(category) ? "" : category;
    }

    public String getSize() {
        return size == 0 ? "" : "[" + Util.getSize(size) + "]";
    }

    public String getParent() {
        return StringUtils.isEmpty(parent) ? "" : "[" + parent + "]";
    }

    public Item parent(String parent) {
        this.parent = parent;
        return this;
    }

    public String getDisplayName() {
        return StringUtils.join(Arrays.asList(getParent(), getName(), getSize()), " ").trim();
    }

    public String getSortName() {
        return StringUtils.join(Arrays.asList(getParent(), Util.getDigit(getName())), " ").trim();
    }

    @Override
    public int compareTo(Item item) {
        return getSortName().compareTo(item.getSortName());
    }
}
