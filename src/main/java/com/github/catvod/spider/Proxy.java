package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.MultiThread;
import com.github.catvod.utils.ProxyVideo;
import com.github.catvod.utils.Util;
import org.apache.http.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy extends Spider {

    private static int port = -1;

    public static Object[] proxy(Map<String, String> params) throws Exception {
        switch (params.get("do")) {
            case "ck":
                return new Object[]{200, "text/plain; charset=utf-8", new ByteArrayInputStream("ok".getBytes("UTF-8"))};
            case "multi":
                return MultiThread.proxy(params);
            case "ali":
                return Ali.proxy(params);
            case "quark":
                return Quark.proxy(params);
            case "uc":
                return UC.proxy(params);
            case "bili":
                return Bili.proxy(params);
            case "webdav":
//                return WebDAV.vod(params);
            case "bd":
                 return BD.Companion.proxyLocal(params);
            case "proxy":
                return commonProxy(params);
            default:
                return null;
        }
    }

    private static final List<String> keys = Arrays.asList("url", "header", "do", HttpHeaders.USER_AGENT, HttpHeaders.CONTENT_TYPE, HttpHeaders.HOST);
    private static Object[] commonProxy(Map<String, String> params) throws Exception {
        String url = Util.base64Decode(params.get("url"));
        Map<String,String> header = Json.parseSafe(Util.base64Decode(params.get("header")), Map.class);
        if(header == null) header = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if(!keys.contains(entry.getKey())) header.put(entry.getKey(), entry.getValue());
        }
        return new Object[]{ProxyVideo.proxyResponse(url, header)};
    }

    static void adjustPort() {
        if (Proxy.port > 0) return;
        int pt = 9978;
        while (pt < 10000) {
            try {
                String resp = OkHttp.string("http://127.0.0.1:" + pt + "/proxy?do=ck", null);
                if (resp.equals("ok")) {
                    SpiderDebug.log("Found local server port " + pt);
                    Proxy.port = pt;
                    break;
                }
                pt++;
            } catch (Exception e) {
                SpiderDebug.log("请求端口 异常：" + e.getMessage());
            }
        }
    }

    public static String getHostPort() {
        adjustPort();
        return "http://127.0.0.1:" + port;
    }

    public static String getProxyUrl() {
        return getHostPort() + "/proxy";
    }
}
