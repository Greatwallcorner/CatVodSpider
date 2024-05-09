package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.MultiThread;

import java.io.ByteArrayInputStream;
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
            case "bili":
//                return Bili.proxy(params);
            case "webdav":
//                return WebDAV.vod(params);
            case "bd":
                 return BD.Companion.proxyLocal(params);
            default:
                return null;
        }
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
