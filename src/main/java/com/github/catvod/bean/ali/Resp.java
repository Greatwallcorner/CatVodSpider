package com.github.catvod.bean.ali;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class Resp {

    @SerializedName("responses")
    private List<Resp> responses;
    @SerializedName("body")
    private Body body;
    @SerializedName("id")
    private String id;
    @SerializedName("status")
    private int status;

    public static Resp objectFrom(String str) {
        return new Gson().fromJson(str, Resp.class);
    }

    public List<Resp> getResponses() {
        return responses == null ? Collections.emptyList() : responses;
    }

    public Resp getResponse() {
        return getResponses().isEmpty() ? new Resp() : getResponses().get(0);
    }

    public Body getBody() {
        return body == null ? new Body() : body;
    }

    public String getId() {
        return StringUtils.isEmpty(id) ? "" : id;
    }

    public int getStatus() {
        return status;
    }

    public static class Body {

        @SerializedName("domain_id")
        private String domainId;
        @SerializedName("drive_id")
        private String driveId;
        @SerializedName("file_id")
        private String fileId;
        @SerializedName("code")
        private String code;
        @SerializedName("message")
        private String message;

        public String getDomainId() {
            return StringUtils.isEmpty(domainId) ? "" : domainId;
        }

        public String getDriveId() {
            return StringUtils.isEmpty(driveId) ? "" : driveId;
        }

        public String getFileId() {
            return StringUtils.isEmpty(fileId) ? "" : fileId;
        }

        public String getCode() {
            return StringUtils.isEmpty(code) ? "" : code;
        }

        public String getMessage() {
            return StringUtils.isEmpty(message) ? "" : message;
        }
    }
}
