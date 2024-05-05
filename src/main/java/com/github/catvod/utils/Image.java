package com.github.catvod.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

public class Image {

    public static final String FOLDER = "https://x.imgs.ovh/x/2023/09/05/64f680bb030b4.png";
    public static final String VIDEO = "https://x.imgs.ovh/x/2023/09/05/64f67fe2e7720.png";

    public static String getIcon(boolean folder) {
        return folder ? FOLDER : VIDEO;
    }

    public static class UrlHeaderBuilder {
        private String format = "@%s=%s";
        String base = "";
        String param = "";
        public UrlHeaderBuilder(String baseUrl){
            base = baseUrl;
        }

        /**
         *
         * @param header
         * @param value
         * @return
         */
        public UrlHeaderBuilder append(String header, String value){
            if(StringUtils.isNoneBlank(header) && StringUtils.isNoneBlank(value)){
                param += String.format(format, header, value);
            }
            return this;
        }

        public UrlHeaderBuilder referer(String value){
            if(StringUtils.isNoneBlank(value)){
                param += String.format(format, HttpHeaders.REFERER, value);
            }
            return this;
        }

        public UrlHeaderBuilder userAgent(String value){
            if(StringUtils.isNoneBlank(value)){
                param += String.format(format, HttpHeaders.USER_AGENT, value);
            }
            return this;
        }

        public String build(){
            return StringUtils.isBlank(base) ? "" : base + param;
        }
    }
}
