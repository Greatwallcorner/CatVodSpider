package com.github.catvod.api;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.uc.Cache;
import com.github.catvod.bean.uc.Item;
import com.github.catvod.bean.uc.ShareData;
import com.github.catvod.bean.uc.User;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.Proxy;
import com.github.catvod.utils.*;
import com.google.gson.Gson;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UCApi {
    private String apiUrl = "https://pc-api.uc.cn/1/clouddrive/";
    private String cookie = "";
    private String cookieToken = "";
    private String ckey = "";
    private Map<String, Map<String, Object>> shareTokenCache = new HashMap<>();
    private String pr = "pr=UCBrowser&fr=pc";
    private List<String> subtitleExts = Arrays.asList(".srt", ".ass", ".scc", ".stl", ".ttml");
    private Map<String, String> saveFileIdCaches = new HashMap<>();
    private String saveDirId = null;
    private final String saveDirName = "TV";
    private boolean isVip = false;
    private final Cache cache;
    private final Cache tokenCache;
    private ScheduledExecutorService service;


    private JDialog dialog;
    private String serviceTicket;
    private UCTokenHandler qrCodeHandler;

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36");
        headers.put("Referer", "https://drive.uc.cn");
        headers.put("Content-Type", "application/json");
        headers.put("Cookie", cookie);
        //headers.put("Host", "drive-pc.quark.cn");
        return headers;
    }

    private Map<String, String> getWebHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36");
        headers.put("Referer", "https://drive.uc.cn");
        headers.put("Cookie", cookie);
        return headers;
    }

    /* cookieToken = qrCodeHandler.startUC_TOKENScan();
             SpiderDebug.log("扫码登录获取到的cookieToken: " + cookieToken);*/


    private UCApi() {
        qrCodeHandler = new UCTokenHandler();
        cache = Cache.objectFrom(Path.read(getCache()));
        tokenCache = Cache.objectFrom(Path.read(qrCodeHandler.getCache()));

        this.cookieToken = tokenCache.getUser().getCookie();
        SpiderDebug.log("UC初始化获取到的cookieToken: " + cookieToken);
    }

    private static class Loader {
        static volatile UCApi INSTANCE = new UCApi();
    }

    public static UCApi get() {
        return Loader.INSTANCE;
    }

    //从配置中获取cookie
    public void setCookie(String token) throws Exception {
        if (StringUtils.isNoneBlank(token)) {
            this.cookie = token;
            initUserInfo();
        }
    }

    /**
     * 初始化UC信息
     */
    private void initUserInfo() {
        try {
            SpiderDebug.log("uc initUserInfo...");

            //extend没有cookie，从缓存中获取
            if (StringUtils.isAllBlank(cookie)) {
                SpiderDebug.log("uc cookie from ext is empty...");
                cookie = cache.getUser().getCookie();
            }
            //获取到cookie，初始化uc，并且把cookie缓存一次
            if (StringUtils.isNoneBlank(cookie) && cookie.contains("__pus")) {
                SpiderDebug.log(" initUc ...");
                initUc(this.cookie);
                cache.setUser(User.objectFrom(this.cookie));
                return;
            }

            //没有cookie，也没有serviceTicket，抛出异常，提示用户重新登录
            if (StringUtils.isAllBlank(cookie) && StringUtils.isAllBlank(serviceTicket)) {
                SpiderDebug.log("uccookie为空");
                throw new RuntimeException("uccookie为空");
            }

            String token = serviceTicket;
            OkResult result = OkHttp.get("https://drive.uc.cn/account/info?st=" + token + "", new HashMap<>(), getWebHeaders());
            Map json = Json.parseSafe(result.getBody(), Map.class);
            if (json.get("success").equals(Boolean.TRUE)) {
                List<String> cookies = result.getResp().get("set-Cookie");
                List<String> cookieList = new ArrayList<>();
                for (String cookie : cookies) {
                    cookieList.add(cookie.split(";")[0]);
                }
                this.cookie += Util.stringJoin(cookieList, ";");

                cache.setUser(User.objectFrom(this.cookie));
                if (cache.getUser().getCookie().isEmpty()) throw new Exception(this.cookie);
                initUc(this.cookie);
            }

        } catch (Exception e) {
            cache.getUser().clean();
            e.printStackTrace();
            stopService();
            startFlow();
        } finally {
            while (cache.getUser().getCookie().isEmpty()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void initUc(String cookie) throws Exception {
        this.ckey = Util.MD5(cookie);
        this.cookie = cookie;
        this.isVip = getVip();
    }


    public File getCache() {
        return Path.tv("uc");
    }


    public Vod getVod(ShareData shareData) throws Exception {
        getShareToken(shareData);
        List<Item> files = new ArrayList<>();
        List<Item> subs = new ArrayList<>();
        List<Map<String, Object>> listData = listFile(1, shareData, files, subs, shareData.getShareId(), shareData.getFolderId(), 1);

        List<String> playFrom = UCApi.get().getPlayFormatList();
        List<String> playFromtmp = new ArrayList<>();
        playFromtmp.add("uc原画");
        for (String s : playFrom) {
            playFromtmp.add("uc" + s);
        }
        List<String> playUrl = new ArrayList<>();

        if (files.isEmpty()) {
            return null;
        }
        for (int i = 0; i < files.get(files.size() - 1).getShareIndex(); i++) {
            for (int index = 0; index < playFromtmp.size(); index++) {
                List<String> vodItems = new ArrayList<>();
                for (Item video_item : files) {
                    if (video_item.getShareIndex() == i + 1) {
                        vodItems.add(video_item.getEpisodeUrl("电影"));// + findSubs(video_item.getName(), subs));
                    }
                }
                playUrl.add(Util.stringJoin(vodItems, "#"));
            }
        }


        Vod vod = new Vod();
        vod.setVodId("");
        vod.setVodContent("");
        vod.setVodPic("");
        vod.setVodName("");
        vod.setVodPlayUrl(Util.stringJoin(playUrl, "$$$"));
        vod.setVodPlayFrom(Util.stringJoin(playFromtmp, "$$$"));
        vod.setTypeName("uc云盘");
        return vod;
    }

    public String playerContent(String[] split, String flag) throws Exception {
        SpiderDebug.log("flag:" + flag);
        String fileId = split[0], fileToken = split[1], shareId = split[2], stoken = split[3];
        String playUrl = "";
        if (flag.contains("uc原画")) {
            playUrl = this.getDownload(shareId, stoken, fileId, fileToken, true);
        } else {
            playUrl = this.getLiveTranscoding(shareId, stoken, fileId, fileToken, flag);
        }
        SpiderDebug.log("origin playUrl:" + playUrl);
        Map<String, String> header = getHeaders();
        header.remove("Host");
        header.remove("Content-Type");

        //UCTV 可以直接播放，不需要代理
        if (testVideo(playUrl)) {
            SpiderDebug.log("UCTV 可以直接播放，不需要代理" );

            return Result.get().url(playUrl).string();
        }
        return Result.get().url(proxyVideoUrl(playUrl, header)).octet().header(header).string();
    }

    private boolean testVideo(String url) {

        OkResult okResult1 = OkHttp.get(url, new HashMap<>(), Map.of("Range", "bytes=0-0"));
        return okResult1.getCode() == 206;

    }

    private String proxyVideoUrl(String url, Map<String, String> header) {
        return String.format(Proxy.getProxyUrl() + "?do=uc&type=video&url=%s&header=%s", Util.base64Encode(url.getBytes(Charset.defaultCharset())), Util.base64Encode(Json.toJson(header).getBytes(Charset.defaultCharset())));
    }

    public Object[] proxyVideo(Map<String, String> params) throws Exception {
        String url = Util.base64Decode(params.get("url"));
        SpiderDebug.log("proxy url :" + url);
        SpiderDebug.log("proxy header :" + Util.base64Decode(params.get("header")));
        Map header = new Gson().fromJson(Util.base64Decode(params.get("header")), Map.class);
        if (header == null) header = new HashMap<>();
        List<String> arr = List.of("Range", "Accept", "Accept-Encoding", "Accept-Language", "Cookie", "Origin", "Referer", "Sec-Ch-Ua", "Sec-Ch-Ua-Mobile", "Sec-Ch-Ua-Platform", "Sec-Fetch-Dest", "Sec-Fetch-Mode", "Sec-Fetch-Site", "User-Agent");
        for (String key : params.keySet()) {
            for (String s : arr) {
                if (s.toLowerCase().equals(key.toLowerCase())) {
                    header.put(key, params.get(key));
                }
            }

        }
        if (Util.getExt(url).contains("m3u8")) {
            return getM3u8(url, header);
        }
        return new Object[]{ProxyVideo.proxyResponse(url, header)};
    }

    /**
     * 代理m3u8
     *
     * @param url
     * @param header
     * @return
     */
    private Object[] getM3u8(String url, Map header) {
        SpiderDebug.log("m3u8 url  :" + url);
        OkResult result = OkHttp.get(url, new HashMap<>(), header);
        String[] m3u8Arr = result.getBody().split("\n");
        List<String> listM3u8 = new ArrayList<>();

        String site = url.substring(0, url.lastIndexOf("/")) + "/";
        int mediaId = 0;
        for (String oneLine : m3u8Arr) {
            String thisOne = oneLine;

            if (oneLine.contains(".ts")) {
                mediaId++;
                thisOne = proxyVideoUrl(site + thisOne, header);
                SpiderDebug.log("m3u8 line " + mediaId + ":" + oneLine);
                SpiderDebug.log("m3u8 proxyed line " + mediaId + " :" + thisOne);

            }
            listM3u8.add(thisOne);
        }
        String m3u8Str = Util.stringJoin("\n", listM3u8);
        String contentType = result.getResp().get("Content-Type").get(0);

        Map<String, String> respHeaders = new HashMap<>();
        //  respHeaders.put("Access-Control-Allow-Origin","*");
        //    respHeaders.put("Access-Control-Allow-Credentials","true");
        for (String key : result.getResp().keySet()) {
            respHeaders.put(key, result.getResp().get(key).get(0));
        }
        return new Object[]{result.getCode(), contentType, new ByteArrayInputStream(m3u8Str.getBytes(Charset.defaultCharset())), respHeaders};
    }

    /**
     * @param url
     * @param params get 参数
     * @param data   post json
     * @param retry
     * @param method
     * @return
     * @throws Exception
     */
    private String api(String url, Map<String, String> params, Map<String, Object> data, Integer retry, String method) throws Exception {


        int leftRetry = retry != null ? retry : 3;
        if (StringUtils.isAllBlank(cookie)) {
            this.initUserInfo();
            return api(url, params, data, leftRetry - 1, method);
        }
        OkResult okResult;
        if ("GET".equals(method)) {
            okResult = OkHttp.get(this.apiUrl + url, params, getHeaders());
        } else {
            okResult = OkHttp.post(this.apiUrl + url, Json.toJson(data), getHeaders());
        }
        if (okResult.getResp().get("Set-Cookie") != null) {
            Matcher matcher = Pattern.compile("__puus=([^;]+)").matcher(Util.stringJoin(okResult.getResp().get("Set-Cookie"), ";;;"));
            if (matcher.find()) {
                Matcher cookieMatcher = Pattern.compile("__puus=([^;]+)").matcher(this.cookie);
                if (cookieMatcher.find() && !cookieMatcher.group(1).equals(matcher.group(1))) {
                    this.cookie = this.cookie.replaceAll("__puus=[^;]+", "__puus=" + matcher.group(1));
                } else {
                    this.cookie = this.cookie + ";__puus=" + matcher.group(1);
                }
            }
        }

        if (okResult.getCode() != 200 && leftRetry > 0) {
            SpiderDebug.log("api error code:" + okResult.getCode());
            Thread.sleep(1000);
            return api(url, params, data, leftRetry - 1, method);
        }
        return okResult.getBody();
    }


    /**
     * 获取二维码登录的令牌
     *
     * @return 返回包含二维码登录令牌的字符串
     */
    private String getTokenForQrcodeLogin() {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", "381");
        params.put("v", "1.2");
        params.put("request_id", UUID.randomUUID().toString());
        OkResult res = OkHttp.post("https://api.open.uc.cn/cas/ajax/getTokenForQrcodeLogin?__dt=" + RandomUtils.nextInt(1000, 100000) + "&__t=" + new Date().getTime(), params, new HashMap<>());
        if (this.cookie.isEmpty()) {
            List<String> cookies = res.getResp().get("set-Cookie");
            List<String> cookieList = new ArrayList<>();
            for (String cookie : cookies) {
                cookieList.add(cookie.split(";")[0]);
            }
            this.cookie = Util.stringJoin(cookieList, ";");
        }
        Map<String, Object> json = Json.parseSafe(res.getBody(), Map.class);
        if (Objects.equals(json.get("message"), "ok")) {
            return (String) ((Map<String, Object>) ((Map<String, Object>) json.get("data")).get("members")).get("token");
        }
        return "";
    }


    /**
     * 获取二维码内容
     * <p>
     * 此方法用于生成二维码的URL内容该URL用于二维码登录，包含了登录所需的token和客户端信息
     *
     * @return 返回包含token的二维码URL字符串
     */
    private String getQrCodeToken() {
        // 获取用于二维码登录的token
        String token = getTokenForQrcodeLogin();
        // 组装二维码URL，包含token和客户端标识
        return token;
    }

    private void startFlow() {
        Init.execute(this::showInput);
    }

    private void showInput() {
        try {
            JPanel jPanel = new JPanel();
            jPanel.setSize(Swings.dp2px(200), Swings.dp2px(80));

            JTextField textField = new JTextField();
            textField.setName("token");
            textField.setColumns(Swings.dp2px(38));
            JButton button = new JButton("Ok");
            jPanel.add(textField);
            jPanel.add(button);

            JButton qrButton = new JButton("QRCode");
            jPanel.add(qrButton);
            JDialog jDialog = Util.showDialog(jPanel, "输入token");
            button.addActionListener((event) -> {
                onPositive(textField.getText());
                jDialog.dispose();
            });
            qrButton.addActionListener((event) -> {
                SwingUtilities.invokeLater(this::getQRCode);
                jDialog.dispose();
            });
        } catch (Exception ignored) {
        }
//        try {
//            int margin = ResUtil.dp2px(16);
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            FrameLayout frame = new FrameLayout(Init.context());
//            params.setMargins(margin, margin, margin, margin);
//            EditText input = new EditText(Init.context());
//            frame.addView(input, params);
//            dialog = new AlertDialog.Builder(Init.getActivity()).setTitle("请输入UC cookie").setView(frame).setNeutralButton("QRCode", (dialog, which) -> onNeutral()).setNegativeButton(android.R.string.cancel, null).setPositiveButton(android.R.string.ok, (dialog, which) -> onPositive(input.getText().toString())).show();
//        } catch (Exception ignored) {
//        }
    }

    private void onNeutral() {
        dismiss();
        Init.execute(this::getQRCode);
    }

    private void onPositive(String text) {
        dismiss();
        Init.execute(() -> {
            if (text.startsWith("http")) setToken(OkHttp.string(text));
            else setToken(text);
        });
    }

    private void getQRCode() {
        String token = getQrCodeToken();

        Init.run(() -> openApp(token));
    }

    private void openApp(String token) {
        try {
            showQRCode("https://su.uc.cn/1_n0ZCv?uc_param_str=dsdnfrpfbivesscpgimibtbmnijblauputogpintnwktprchmt&token=" + token + "&client_id=381&uc_biz_str=S%3Acustom%7CC%3Atitlebar_fix");
        } finally {
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            Init.execute(() -> startService(map));
        }
    }

    private void showQRCode(String content) {
        try {
            int size = 300;
            SwingUtilities.invokeLater(() -> {
                BufferedImage bitmap = QRCode.getBitmap(content, size, 2);
                JPanel jPanel = new JPanel();
                jPanel.setSize(Swings.dp2px(size), Swings.dp2px(size));
                jPanel.add(new JLabel(new ImageIcon(bitmap)));
                dialog = Util.showDialog(jPanel, "请使用UC网盘app扫描");
            });
            Util.notify("请使用uc网盘App扫描二维码");
        } catch (Exception ignored) {
        }
    }

    private void startService(Map<String, String> params) {
        SpiderDebug.log("----startservice");
        params.put("client_id", "381");
        params.put("v", "1.2");
        params.put("request_id", UUID.randomUUID().toString());
        service = Executors.newScheduledThreadPool(1);

        service.scheduleWithFixedDelay(() -> {
            SpiderDebug.log("----scheduleAtFixedRate" + new Date().toString());
            String result = OkHttp.string("https://api.open.uc.cn/cas/ajax/getServiceTicketByQrcodeToken?__dt=" + RandomUtils.nextInt(1000, 100000) + "&__t=" + new Date().getTime(), params, getWebHeaders());
            Map<String, Object> json = Json.parseSafe(result, Map.class);
            if (json.get("status").equals(Double.valueOf(2000000))) {
                setToken((String) ((Map<String, Object>) ((Map<String, Object>) json.get("data")).get("members")).get("service_ticket"));

            }

        }, 1, 3, TimeUnit.SECONDS);
    }

    private void setToken(String value) {
        this.serviceTicket = value;
        SpiderDebug.log("ServiceTicket:" + value);
        Notify.show("ServiceTicket:" + value);
        initUserInfo();
        stopService();
    }

    private void stopService() {
        if (service != null) service.shutdownNow();


        Init.run(this::dismiss);
    }

//    private void dismiss(DialogInterface dialog) {
//        stopService();
//    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dispose();
        } catch (Exception ignored) {
        }
    }

    private boolean getVip() throws Exception {
        Map<String, Object> listData = Json.parseSafe(api("member?pr=ucpro&fr=pc&uc_param_str=&fetch_subscribe=true&_ch=home&fetch_identity=true", null, null, 0, "GET"), Map.class);
        return false;//((Map<String, String>) listData.get("data")).get("member_type").contains("VIP");
    }

    public List<String> getPlayFormatList() {
        if (this.isVip) {
            return Arrays.asList("4K", "超清", "高清", "普画");
        } else {
            return Collections.singletonList("普画");
        }
    }

    private List<String> getPlayFormatUcList() {
        if (this.isVip) {
            return Arrays.asList("4k", "2k", "super", "high", "normal", "low");
        } else {
            return Collections.singletonList("low");
        }
    }

    public ShareData getShareData(String url) {
        Pattern pattern = Pattern.compile("https://drive\\.uc\\.cn/s/([^?]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return new ShareData(matcher.group(1), "0");
        }
        return null;
    }


    private void getShareToken(ShareData shareData) throws Exception {
        if (!this.shareTokenCache.containsKey(shareData.getShareId())) {
            this.shareTokenCache.remove(shareData.getShareId());
            Map<String, Object> shareToken = Json.parseSafe(api("share/sharepage/token?" + this.pr, Collections.emptyMap(), Map.of("pwd_id", shareData.getShareId(), "passcode", shareData.getSharePwd() == null ? "" : shareData.getSharePwd()), 0, "POST"), Map.class);
            if (shareToken.containsKey("data") && ((Map<String, Object>) shareToken.get("data")).containsKey("stoken")) {
                this.shareTokenCache.put(shareData.getShareId(), (Map<String, Object>) shareToken.get("data"));
            }
        }
    }

    private List<Map<String, Object>> listFile(int shareIndex, ShareData shareData, List<Item> videos, List<Item> subtitles, String shareId, String folderId, Integer page) throws Exception {
        int prePage = 200;
        page = page != null ? page : 1;

        Map<String, Object> listData = Json.parseSafe(api("share/sharepage/detail?" + this.pr + "&pwd_id=" + shareId + "&stoken=" + encodeURIComponent((String) this.shareTokenCache.get(shareId).get("stoken")) + "&pdir_fid=" + folderId + "&force=0&_page=" + page + "&_size=" + prePage + "&_sort=file_type:asc,file_name:asc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") == null) return Collections.emptyList();
        List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list");
        if (items == null) return Collections.emptyList();
        List<Map<String, Object>> subDir = new ArrayList<>();
        for (Map<String, Object> item : items) {
            if (Boolean.TRUE.equals(item.get("dir"))) {
                subDir.add(item);
            } else if (Boolean.TRUE.equals(item.get("file")) && "video".equals(item.get("obj_category"))) {
                if ((Double) item.get("size") < 1024 * 1024 * 5) continue;
                item.put("stoken", this.shareTokenCache.get(shareData.getShareId()).get("stoken"));
                videos.add(Item.objectFrom(item, shareData.getShareId(), shareIndex));
            } else if ("file".equals(item.get("type")) && this.subtitleExts.contains("." + Util.getExt((String) item.get("file_name")))) {
                subtitles.add(Item.objectFrom(item, shareData.getShareId(), shareIndex));
            }
        }
        if (page < Math.ceil((double) ((Map<String, Object>) listData.get("metadata")).get("_total") / prePage)) {
            List<Map<String, Object>> nextItems = listFile(shareIndex, shareData, videos, subtitles, shareId, folderId, page + 1);
            items.addAll(nextItems);
        }
        for (Map<String, Object> dir : subDir) {
            List<Map<String, Object>> subItems = listFile(shareIndex, shareData, videos, subtitles, shareId, dir.get("fid").toString(), null);
            items.addAll(subItems);
        }
        return items;
    }

    private Map<String, Object> findBestLCS(Item mainItem, List<Item> targetItems) {
        List<Map<String, Object>> results = new ArrayList<>();
        int bestMatchIndex = 0;
        for (int i = 0; i < targetItems.size(); i++) {
            Util.LCSResult currentLCS = Util.lcs(mainItem.getName(), targetItems.get(i).getName());
            Map<String, Object> result = new HashMap<>();
            result.put("target", targetItems.get(i));
            result.put("lcs", currentLCS);
            results.add(result);
            if (currentLCS.length > results.get(bestMatchIndex).get("lcs").toString().length()) {
                bestMatchIndex = i;
            }
        }
        Map<String, Object> bestMatch = results.get(bestMatchIndex);
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("allLCS", results);
        finalResult.put("bestMatch", bestMatch);
        finalResult.put("bestMatchIndex", bestMatchIndex);
        return finalResult;
    }

    public void getFilesByShareUrl(int shareIndex, String shareInfo, List<Item> videos, List<Item> subtitles) throws Exception {
        ShareData shareData = getShareData((String) shareInfo);
        if (shareData == null) return;
        getShareToken(shareData);
        if (!this.shareTokenCache.containsKey(shareData.getShareId())) return;
        listFile(shareIndex, shareData, videos, subtitles, shareData.getShareId(), shareData.getFolderId(), 1);
        if (!subtitles.isEmpty()) {
            for (Item video : videos) {
                Map<String, Object> matchSubtitle = findBestLCS(video, subtitles);
                if (matchSubtitle.get("bestMatch") != null) {
                    video.setSubtitle((String) ((Map<String, Object>) matchSubtitle.get("bestMatch")).get("target"));
                }
            }
        }
    }

    private void clean() {
        saveFileIdCaches.clear();
    }

    private void clearSaveDir() throws Exception {

        Map<String, Object> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=" + this.saveDirId + "&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") != null && ((List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list")).size() > 0) {
            List<String> list = new ArrayList<>();
            for (Map<String, Object> stringStringMap : ((List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list"))) {
                list.add((String) stringStringMap.get("fid"));
            }
            api("file/delete?" + this.pr, Collections.emptyMap(), Map.of("action_type", "2", "filelist", Json.toJson(list), "exclude_fids", ""), 0, "POST");
        }
    }

    private void createSaveDir(boolean clean) throws Exception {
        if (this.saveDirId != null) {
            if (clean) clearSaveDir();
            return;
        }

        Map<String, Object> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=0&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") != null) {
            for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list")) {
                if (this.saveDirName.equals(item.get("file_name"))) {
                    this.saveDirId = item.get("fid").toString();
                    clearSaveDir();
                    break;
                }
            }
        }
        if (this.saveDirId == null) {
            Map<String, Object> create = Json.parseSafe(api("file?" + this.pr, Collections.emptyMap(), Map.of("pdir_fid", "0", "file_name", this.saveDirName, "dir_path", "", "dir_init_lock", "false"), 0, "POST"), Map.class);
            if (create.get("data") != null && ((Map<String, Object>) create.get("data")).get("fid") != null) {
                this.saveDirId = ((Map<String, Object>) create.get("data")).get("fid").toString();
            }
        }
    }

    private String save(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        createSaveDir(clean);
        if (clean) {
            clean();
        }
        if (this.saveDirId == null) return null;
        if (stoken == null) {
            getShareToken(new ShareData(shareId, null));
            if (!this.shareTokenCache.containsKey(shareId)) return null;
        }

        Map<String, Object> saveResult = Json.parseSafe(api("share/sharepage/save?" + this.pr, null, Map.of("fid_list", List.of(fileId), "fid_token_list", List.of(fileToken), "to_pdir_fid", this.saveDirId, "pwd_id", shareId, "stoken", stoken != null ? stoken : (String) this.shareTokenCache.get(shareId).get("stoken"), "pdir_fid", "0", "scene", "link"), 0, "POST"), Map.class);
        if (saveResult.get("data") != null && ((Map<Object, Object>) saveResult.get("data")).get("task_id") != null) {
            int retry = 0;
            while (true) {

                Map<String, Object> taskResult = Json.parseSafe(api("task?" + this.pr + "&task_id=" + ((Map<String, Object>) saveResult.get("data")).get("task_id") + "&retry_index=" + retry, Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), Map.class);
                if (taskResult.get("data") != null && ((Map<Object, Object>) taskResult.get("data")).get("save_as") != null && ((Map<Object, Object>) ((Map<Object, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids") != null && ((List<String>) ((Map<String, Object>) ((Map<String, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids")).size() > 0) {
                    return ((List<String>) ((Map<String, Object>) ((Map<Object, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids")).get(0);
                }
                retry++;
                if (retry > 2) break;
                Thread.sleep(1000);
            }
        }
        return null;
    }

    private String getLiveTranscoding(String shareId, String stoken, String fileId, String fileToken, String flag) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, true);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }

        Map<String, Object> transcoding = Json.parseSafe(api("file/v2/play?" + this.pr, Collections.emptyMap(), Map.of("fid", this.saveFileIdCaches.get(fileId), "resolutions", "normal,low,high,super,2k,4k", "supports", "fmp4"), 0, "POST"), Map.class);
        if (transcoding.get("data") != null && ((Map<Object, Object>) transcoding.get("data")).get("video_list") != null) {
            String flagId = flag.split("-")[flag.split("-").length - 1];
            int index = Util.findAllIndexes(getPlayFormatList(), flagId);
            String ucFormat = getPlayFormatUcList().get(index);
            for (Map<String, Object> video : (List<Map<String, Object>>) ((Map<Object, Object>) transcoding.get("data")).get("video_list")) {
                if (video.get("resolution").equals(ucFormat)) {
                    return (String) ((Map<String, Object>) video.get("video_info")).get("url");
                }
            }
            return (String) ((Map<String, Object>) ((List<Map<String, Object>>) ((Map<Object, Object>) transcoding.get("data")).get("video_list")).get(index).get("video_info")).get("url");
        }
        return null;
    }

    private String getDownload(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, clean);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }

        //token不为空
        if (StringUtils.isNoneBlank(cookieToken)) {
            SpiderDebug.log("cookieToken不为空: " + cookieToken + ";开始下载");
            return qrCodeHandler.download(cookieToken, this.saveFileIdCaches.get(fileId));
        } else {
            Map<String, Object> down = Json.parseSafe(api("file/download?" + this.pr + "&uc_param_str=", Collections.emptyMap(), Map.of("fids", List.of(this.saveFileIdCaches.get(fileId))), 0, "POST"), Map.class);
            if (down.get("data") != null) {
                return ((List<Map<String, Object>>) down.get("data")).get(0).get("download_url").toString();
            }
        }


        return null;
    }

    // Helper method to convert bytes to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Encoding helper method
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }


}

