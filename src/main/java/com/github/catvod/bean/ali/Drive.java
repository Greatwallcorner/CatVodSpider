package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

public class Drive {

    @SerializedName("default_drive_id")
    private String defaultDriveId;
    @SerializedName("resource_drive_id")
    private String resourceDriveId;

    public static Drive objectFrom(String str) {
        Drive item = new Gson().fromJson(str, Drive.class);
        return item == null ? new Drive() : item;
    }

    private String getDefaultDriveId() {
        return StringUtils.isEmpty(defaultDriveId) ? "" : defaultDriveId;
    }

    private String getResourceDriveId() {
        return StringUtils.isEmpty(resourceDriveId) ? "" : resourceDriveId;
    }

    public String getDriveId() {
        return getResourceDriveId().isEmpty() ? getDefaultDriveId() : getResourceDriveId();
    }
}
