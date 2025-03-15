package com.github.catvod.api;

import cn.hutool.crypto.digest.DigestUtil;
import com.github.catvod.bean.uc.Cache;
import com.github.catvod.bean.uc.User;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.*;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UCTokenHandler {
    private static final String CLIENT_ID = "5acf882d27b74502b7040b0c65519aa7";
    private static final String SIGN_KEY = "l3srvtd7p42l0d0x1u8d7yc8ye9kki4d";
    private static final String API_URL = "https://open-api-drive.uc.cn";
    private static final String CODE_API_URL = "http://api.extscreen.com/ucdrive";


    private Map<String, Object> platformStates = new HashMap<>();
    private Map<String, String> addition = new HashMap<>();
    private Map<String, String> conf = new HashMap<>();


    private ScheduledExecutorService service;
    private JDialog dialog;
    private final Cache cache;

    public File getCache() {
        return Path.tv("uctoken");
    }

    public UCTokenHandler() {
        addition.put("DeviceID", "07b48aaba8a739356ab8107b5e230ad4");
        conf.put("api", API_URL);
        conf.put("clientID", CLIENT_ID);
        conf.put("signKey", SIGN_KEY);
        conf.put("appVer", "1.6.8");
        conf.put("channel", "UCTVOFFICIALWEB");
        conf.put("codeApi", CODE_API_URL);
        cache = Cache.objectFrom(Path.read(getCache()));
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateDeviceID(String timestamp) {

        return Util.MD5(timestamp).substring(0, 16);
    }

    private String generateReqId(String deviceID, String timestamp) {

        return Util.MD5(deviceID + timestamp).substring(0, 16);
    }

    private String generateXPanToken(String method, String pathname, String timestamp, String key) {

        return DigestUtil.sha256Hex(method + "&" + pathname + "&" + timestamp + "&" + key);
    }

    public String startUC_TOKENScan() throws Exception {
        String pathname = "/oauth/authorize";
        String timestamp = String.valueOf(new Date().getTime() / 1000 + 1) + "000";
        String deviceID = StringUtils.isNoneBlank((String) addition.get("DeviceID")) ? (String) addition.get("DeviceID") : generateDeviceID(timestamp);
        String reqId = generateReqId(deviceID, timestamp);
        String token = generateXPanToken("GET", pathname, timestamp, (String) conf.get("signKey"));

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 13; zh-cn; M2004J7AC Build/UKQ1.231108.001) AppleWebKit/533.1 (KHTML, like Gecko) Mobile Safari/533.1");
        headers.put("x-pan-tm", timestamp);
        headers.put("x-pan-token", token);
        headers.put("x-pan-client-id", (String) conf.get("clientID"));
        headers.put("Host", "open-api-drive.uc.cn");
        Map<String, String> params = new HashMap<>();
        params.put("req_id", reqId);
        params.put("access_token", StringUtils.isNoneBlank((String) addition.get("AccessToken")) ? (String) addition.get("AccessToken") : "");
        params.put("app_ver", (String) conf.get("appVer"));
        params.put("device_id", deviceID);
        params.put("device_brand", "Xiaomi");
        params.put("platform", "tv");
        params.put("device_name", "M2004J7AC");
        params.put("device_model", "M2004J7AC");
        params.put("build_device", "M2004J7AC");
        params.put("build_product", "M2004J7AC");
        params.put("device_gpu", "Adreno (TM) 550");
        params.put("activity_rect", URLEncoder.encode("{}", "UTF-8"));
        params.put("channel", (String) conf.get("channel"));
        params.put("auth_type", "code");
        params.put("client_id", (String) conf.get("clientID"));
        params.put("scope", "netdisk");
        params.put("qrcode", "1");
        params.put("qr_width", "460");
        params.put("qr_height", "460");
        OkResult okResult = OkHttp.get(API_URL + pathname, params, headers);


        JsonObject resData = Json.safeObject(okResult.getBody());
        String queryToken = resData.get("query_token").getAsString();
        String qrCode = resData.get("qr_data").getAsString();

        platformStates.put("UC_TOKEN", new HashMap<String, String>() {{
            put("query_token", queryToken);
            put("request_id", reqId);
        }});
        Init.execute(() -> showQRCode(qrCode));

        Init.execute(this::startService);
        /*Map<String, Object> result = new HashMap<>();
        result.put("qrcode", "data:image/png;base64," + qrCode);
        result.put("status", "NEW");*/
        return qrCode;

    }


    public Map<String, Object> checkUC_TOKENStatus() throws UnsupportedEncodingException {
        Map<String, String> state = (Map<String, String>) platformStates.get("UC_TOKEN");
        if (state == null) {
            return Map.of("status", "EXPIRED");
        }

        String pathname = "/oauth/code";
        String timestamp = String.valueOf(new Date().getTime() / 1000 + 1) + "000";
        String deviceID = StringUtils.isAllBlank((String) addition.get("DeviceID")) ? (String) addition.get("DeviceID") : generateDeviceID(timestamp);
        String reqId = generateReqId(deviceID, timestamp);
        String xPanToken = generateXPanToken("GET", pathname, timestamp, (String) conf.get("signKey"));

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 13; zh-cn; M2004J7AC Build/UKQ1.231108.001) AppleWebKit/533.1 (KHTML, like Gecko) Mobile Safari/533.1");
        headers.put("x-pan-tm", timestamp);
        headers.put("x-pan-token", xPanToken);
        headers.put("x-pan-client-id", (String) conf.get("clientID"));

        Map<String, String> params = new HashMap<>();
        params.put("req_id", reqId);
        params.put("access_token", (String) addition.get("AccessToken"));
        params.put("app_ver", (String) conf.get("appVer"));
        params.put("device_id", deviceID);
        params.put("device_brand", "Xiaomi");
        params.put("platform", "tv");
        params.put("device_name", "M2004J7AC");
        params.put("device_model", "M2004J7AC");
        params.put("build_device", "M2004J7AC");
        params.put("build_product", "M2004J7AC");
        params.put("device_gpu", "Adreno (TM) 550");
        params.put("activity_rect", URLEncoder.encode("{}", "UTF-8"));
        params.put("channel", (String) conf.get("channel"));
        params.put("client_id", (String) conf.get("clientID"));
        params.put("scope", "netdisk");
        params.put("query_token", state.get("query_token"));

        OkResult okResult = OkHttp.get(API_URL + pathname, params, headers);

//扫码成功
        if (okResult.getCode() == 200) {
            JsonObject resData = Json.safeObject(okResult.getBody());
            String code = resData.get("code").getAsString();

            pathname = "/token";
            reqId = generateReqId(deviceID, timestamp);

            Map<String, String> postData = new HashMap<>();
            postData.put("req_id", reqId);
            postData.put("app_ver", (String) conf.get("appVer"));
            postData.put("device_id", deviceID);
            postData.put("device_brand", "Xiaomi");
            postData.put("platform", "tv");
            postData.put("device_name", "M2004J7AC");
            postData.put("device_model", "M2004J7AC");
            postData.put("build_device", "M2004J7AC");
            postData.put("build_product", "M2004J7AC");
            postData.put("device_gpu", "Adreno (TM) 550");
            postData.put("activity_rect", URLEncoder.encode("{}", "UTF-8"));
            postData.put("channel", (String) conf.get("channel"));
            postData.put("code", code);

            OkResult okResult1 = OkHttp.post(conf.get("codeApi") + pathname, Json.toJson(postData), headers);


            if (okResult1.getCode() == 200) {
                JsonObject tokenResData = Json.safeObject(okResult1.getBody());
                platformStates.remove("UC_TOKEN");
                Map<String, Object> result = new HashMap<>();
                result.put("status", "CONFIRMED");
                result.put("cookie", tokenResData.get("data").getAsJsonObject().get("access_token").getAsString());
                SpiderDebug.log("uc Token获取成功：" + tokenResData.get("data").getAsJsonObject().get("access_token").getAsString());

                //保存到本地
                cache.setTokenUser(User.objectFrom(tokenResData.get("data").getAsJsonObject().get("access_token").getAsString()));

                //停止检验线程，关闭弹窗
                stopService();
                return result;
            }

        } else if (okResult.getCode() == 400) {
            return Map.of("status", "NEW");
        }


        platformStates.remove("UC_TOKEN");
        return Map.of("status", "EXPIRED");
    }

    public String download(String token, String saveFileId) throws Exception {
        SpiderDebug.log("开始下载:" + saveFileId + ";token:" + token);
        String pathname = "/file";
        String timestamp = String.valueOf(new Date().getTime() / 1000 + 1) + "000";
        String deviceID = StringUtils.isAllBlank((String) addition.get("DeviceID")) ? (String) addition.get("DeviceID") : generateDeviceID(timestamp);
        String reqId = generateReqId(deviceID, timestamp);
        String xPanToken = generateXPanToken("GET", pathname, timestamp, (String) conf.get("signKey"));

        Map<String, String> headers = new HashMap<>();
        //headers.put("Accept-Encoding", "gzip");
        headers.put("content-type", "text/plain;charset=UTF-8");
        headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 13; zh-cn; M2004J7AC Build/UKQ1.231108.001) AppleWebKit/533.1 (KHTML, like Gecko) Mobile Safari/533.1");
        headers.put("x-pan-tm", timestamp);
        headers.put("x-pan-token", xPanToken);
        headers.put("x-pan-client-id", (String) conf.get("clientID"));


        Map<String, String> params = new HashMap<>();
        params.put("req_id", reqId);
        params.put("access_token", token);
        params.put("app_ver", (String) conf.get("appVer"));
        params.put("device_id", deviceID);
        params.put("device_brand", "Xiaomi");
        params.put("platform", "tv");
        params.put("device_name", "M2004J7AC");
        params.put("device_model", "M2004J7AC");
        params.put("build_device", "M2004J7AC");
        params.put("build_product", "M2004J7AC");
        params.put("device_gpu", "Adreno (TM) 550");
        params.put("activity_rect", URLEncoder.encode("{}", "UTF-8"));
        params.put("channel", (String) conf.get("channel"));
        params.put("method", "streaming");
        params.put("group_by", "source");
        params.put("fid", saveFileId);
        params.put("resolution", "low,normal,high,super,2k,4k");
        params.put("support", "dolby_vision");

        OkResult okResult1 = OkHttp.get(API_URL + pathname, params, headers);
        JsonObject obj = Json.safeObject(okResult1.getBody());
        String downloadUrl = obj.get("data").getAsJsonObject().get("video_info").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
        SpiderDebug.log("uc TV 下载文件内容：" + downloadUrl);
        return downloadUrl;
    }

    /**
     * 显示qrcode
     *
     * @param base64Str
     */
    public void showQRCode(String base64Str) {
        try {
            int size = 300;
            SwingUtilities.invokeLater(() -> {
                BufferedImage bitmap = QRCode.base64StringToImage(base64Str);
                JPanel jPanel = new JPanel();
                jPanel.setSize(Swings.dp2px(size), Swings.dp2px(size));
                jPanel.add(new JLabel(new ImageIcon(bitmap)));
                dialog = Util.showDialog(jPanel, "请使用uc网盘App扫描");
//                dialog.addComponentListener();
            });
            Util.notify("请使用uc网盘App扫描二维码");
        } catch (Exception ignored) {
        }
    }

    private void stopService() {
        if (service != null) {
            SpiderDebug.log("uc线程池关闭");
            service.shutdownNow();
            dialog.dispose();
        }
    }

    public void startService() {
        SpiderDebug.log("----start UC token  service");

        service = Executors.newScheduledThreadPool(1);

        service.scheduleWithFixedDelay(() -> {
            try {
                SpiderDebug.log("----checkUC_TOKENStatus中");

                checkUC_TOKENStatus();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        }, 1, 3, TimeUnit.SECONDS);
    }

}