package com.github.catvod.utils;

public final class VideoFormatConstants {

    private VideoFormatConstants() {
        // 私有构造函数，防止实例化
    }

    // 常见视频格式 MIME 类型（浏览器兼容）
    public static final String MP4 = "video/mp4";
    public static final String WEBM = "video/webm";
    public static final String OGV = "video/ogg";
    public static final String MOV = "video/quicktime";
    public static final String M4V = "video/x-m4v";

    // 视频流格式 MIME 类型
    public static final String HLS = "application/vnd.apple.mpegurl"; // HLS (HTTP Live Streaming)
    public static final String DASH = "application/dash+xml"; // MPEG-DASH
    public static final String RTSP = "application/sdp"; // RTSP Streaming
    public static final String RTP = "video/RTP"; // RTP Streaming

    // 获取所有支持的浏览器兼容视频格式
    public static String[] getAllFormats() {
        return new String[]{MP4, WEBM, OGV, MOV, M4V, HLS, DASH, RTSP, RTP};
    }
}