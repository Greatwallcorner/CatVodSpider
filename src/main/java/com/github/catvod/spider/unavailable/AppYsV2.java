//package com.github.catvod.spider;
//
//import com.github.catvod.crawler.SpiderDebug;
//import com.github.catvod.crawler.SpiderI;
//import com.github.catvod.net.OkHttp;
//import com.github.catvod.utils.Utils;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonIOException;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.apache.commons.lang3.StringUtils;
//
//import java.net.URLEncoder;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * M浏览器中的App影视
// * Author: 群友 不负此生
// */
//public class AppYsV2 extends SpiderI {
//
//    @Override
//    public void init( String extend) throws Exception {
//        init(extend);
//        try {
//            extInfos = extend.split("###");
//        } catch (Exception ignored) {
//        }
//    }
//
//    @Override
//    public String homeContent(boolean filter) throws Exception {
//        String url = getCateUrl(getApiUrl());
//        JsonArray JsonArray = null;
//        if (!url.isEmpty()) {
//            SpiderDebug.log(url);
//            String json = OkHttp.string(url, getHeaders(url));
//            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
//            if (obj.has("list") && obj.get("list") instanceof JsonArray) {
//                JsonArray = obj.getAsJsonArray("list");
//            } else if (obj.has("data") && obj.get("data") instanceof JsonObject && obj.getAsJsonObject("data").has("list") && obj.getAsJsonObject("data").get("list") instanceof JsonArray) {
//                JsonArray = obj.getAsJsonObject("data").getAsJsonArray("list");
//            } else if (obj.has("data") && obj.get("data") instanceof JsonArray) {
//                JsonArray = obj.getAsJsonArray("data");
//            }
//        } else { // 通过filter列表读分类
//            String filterStr = getFilterTypes(url, null);
//            String[] classes = filterStr.split("\n")[0].split("\\+");
//            JsonArray = new JsonArray();
//            for (int i = 1; i < classes.length; i++) {
//                String[] kv = classes[i].trim().split("=");
//                if (kv.length < 2) continue;
//                JsonObject newCls = new JsonObject();
//                newCls.addProperty("type_name", kv[0].trim());
//                newCls.addProperty("type_id", kv[1].trim());
//                JsonArray.add(newCls);
//            }
//        }
//        JsonObject result = new JsonObject();
//        JsonArray classes = new JsonArray();
//        if (JsonArray != null) {
//            for (int i = 0; i < JsonArray.size(); i++) {
//                JsonObject jObj = JsonArray.get(i).getAsJsonObject();
//                String typeName = jObj.get("type_name").getAsString();
//                String typeId = jObj.get("type_id").getAsString();
//                JsonObject newCls = new JsonObject();
//                newCls.addProperty("type_id", typeId);
//                newCls.addProperty("type_name", typeName);
//                JsonObject typeExtend = jObj.getAsJsonObject("type_extend");
//                if (filter) {
//                    String filterStr = getFilterTypes(url, typeExtend);
//                    String[] filters = filterStr.split("\n");
//                    JsonArray filterArr = new JsonArray();
//                    for (int k = url.isEmpty() ? 1 : 0; k < filters.length; k++) {
//                        String l = filters[k].trim();
//                        if (l.isEmpty()) continue;
//                        String[] oneLine = l.split("\\+");
//                        String type = oneLine[0].trim();
//                        String typeN = type;
//                        if (type.contains("筛选")) {
//                            type = type.replace("筛选", "");
//                            switch (type) {
//                                case "class":
//                                    typeN = "类型";
//                                    break;
//                                case "area":
//                                    typeN = "地区";
//                                    break;
//                                case "lang":
//                                    typeN = "语言";
//                                    break;
//                                case "year":
//                                    typeN = "年份";
//                                    break;
//                            }
//                        }
//                        JsonObject jOne = new JsonObject();
//                        jOne.addProperty("key", type);
//                        jOne.addProperty("name", typeN);
//                        JsonArray valueArr = new JsonArray();
//                        for (int j = 1; j < oneLine.length; j++) {
//                            JsonObject kvo = new JsonObject();
//                            String kv = oneLine[j].trim();
//                            int sp = kv.indexOf("=");
//                            if (sp == -1) {
//                                kvo.addProperty("n", kv);
//                                kvo.addProperty("v", kv);
//                            } else {
//                                String n = kv.substring(0, sp);
//                                kvo.addProperty("n", n.trim());
//                                kvo.addProperty("v", kv.substring(sp + 1).trim());
//                            }
//                            valueArr.add(kvo);
//                        }
//                        jOne.add("value", valueArr);
//                        filterArr.add(jOne);
//                    }
//                    if (!result.has("filters")) {
//                        result.add("filters", new JsonObject());
//                    }
//                    result.get("filters").getAsJsonObject().add(typeId, filterArr);
//                }
//                classes.add(newCls);
//            }
//        }
//        result.add("class", classes);
//        return result.toString();
//    }
//
//    @Override
//    public String homeVideoContent() throws Exception {
//        String apiUrl = getApiUrl();
//        String url = getRecommendUrl(apiUrl);
//        boolean isTV = false;
//        if (url.isEmpty()) {
//            url = getCateFilterUrlPrefix(apiUrl) + "movie&page=1&area=&type=&start=";
//            isTV = true;
//        }
//        SpiderDebug.log(url);
//        String json = OkHttp.string(url, getHeaders(url));
//        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
//        JsonArray videos = new JsonArray();
//        if (isTV) {
//            JsonArray JsonArray = obj.getAsJsonArray("data");
//            for (int i = 0; i < JsonArray.size(); i++) {
//                JsonObject vObj = JsonArray.get(i).getAsJsonObject();
//                JsonObject v = new JsonObject();
//                v.addProperty("vod_id", vObj.get("nextlink").getAsString());
//                v.addProperty("vod_name", vObj.get("title").getAsString());
//                v.addProperty("vod_pic", vObj.get("pic").getAsString());
//                v.addProperty("vod_remarks", vObj.get("state").getAsString());
//                videos.add(v);
//            }
//        } else {
//            ArrayList<JsonArray> arrays = new ArrayList<>();
//            findJsonArray(obj, "vlist", arrays);
//            if (arrays.isEmpty()) {
//                findJsonArray(obj, "vod_list", arrays);
//            }
//            List<String> ids = new ArrayList<>();
//            for (JsonArray JsonArray : arrays) {
//                for (int i = 0; i < JsonArray.size(); i++) {
//                    JsonObject vObj = JsonArray.get(i).getAsJsonObject();
//                    String vid = vObj.get("vod_id").getAsString();
//                    if (ids.contains(vid)) continue;
//                    ids.add(vid);
//                    JsonObject v = new JsonObject();
//                    v.addProperty("vod_id", vid);
//                    v.addProperty("vod_name", vObj.get("vod_name").getAsString());
//                    v.addProperty("vod_pic", vObj.get("vod_pic").getAsString());
//                    v.addProperty("vod_remarks", vObj.get("vod_remarks").getAsString());
//                    videos.add(v);
//                }
//            }
//        }
//        JsonObject result = new JsonObject();
//        result.add("list", videos);
//        return result.toString();
//    }
//
//    @Override
//    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
//        String apiUrl = getApiUrl();
//        String url = getCateFilterUrlPrefix(apiUrl) + tid + getCateFilterUrlSuffix(apiUrl);
//        url = url.replace("#PN#", pg);
//        url = url.replace("筛选class", (extend != null && extend.containsKey("class")) ? extend.get("class") : "");
//        url = url.replace("筛选area", (extend != null && extend.containsKey("area")) ? extend.get("area") : "");
//        url = url.replace("筛选lang", (extend != null && extend.containsKey("lang")) ? extend.get("lang") : "");
//        url = url.replace("筛选year", (extend != null && extend.containsKey("year")) ? extend.get("year") : "");
//        url = url.replace("排序", (extend != null && extend.containsKey("排序")) ? extend.get("排序") : "");
//        SpiderDebug.log(url);
//        String json = OkHttp.string(url, getHeaders(url));
//        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
//        int totalPg = Integer.MAX_VALUE;
//        try {
//            if (obj.has("totalpage")) {
//                totalPg = obj.get("totalpage").getAsInt();
//            } else if (obj.has("pagecount")) {
//                totalPg = obj.get("pagecount").getAsInt();
//            } else if (obj.has("data") && obj.get("data") instanceof JsonObject && (obj.getAsJsonObject("data").has("total") && obj.getAsJsonObject("data").get("total") instanceof Integer && obj.getAsJsonObject("data").has("limit") && obj.getAsJsonObject("data").get("limit") instanceof Integer)) {
//                int limit = obj.getAsJsonObject("data").get("limit").getAsInt();
//                int total = obj.getAsJsonObject("data").get("total").getAsInt();
//                totalPg = total % limit == 0 ? (total / limit) : (total / limit + 1);
//            }
//        } catch (Exception e) {
//            SpiderDebug.log(e);
//        }
//
//        JsonArray JsonArray = null;
//        JsonArray videos = new JsonArray();
//        if (obj.has("list") && obj.get("list") instanceof JsonArray) {
//            JsonArray = obj.getAsJsonArray("list");
//        } else if (obj.has("data") && obj.get("data") instanceof JsonObject && obj.getAsJsonObject("data").has("list") && obj.getAsJsonObject("data").get("list") instanceof JsonArray) {
//            JsonArray = obj.getAsJsonObject("data").getAsJsonArray("list");
//        } else if (obj.has("data") && obj.get("data") instanceof JsonArray) {
//            JsonArray = obj.getAsJsonArray("data");
//        }
//        if (JsonArray != null) {
//            for (int i = 0; i < JsonArray.size(); i++) {
//                JsonObject vObj = JsonArray.get(i).getAsJsonObject();
//                if (vObj.has("vod_id")) {
//                    JsonObject v = new JsonObject();
//                    v.addProperty("vod_id", vObj.get("vod_id"));
//                    v.addProperty("vod_name", vObj.get("vod_name"));
//                    v.addProperty("vod_pic", vObj.get("vod_pic"));
//                    v.addProperty("vod_remarks", vObj.get("vod_remarks"));
//                    videos.add(v);
//                } else {
//                    JsonObject v = new JsonObject();
//                    v.addProperty("vod_id", vObj.get("nextlink"));
//                    v.addProperty("vod_name", vObj.get("title"));
//                    v.addProperty("vod_pic", vObj.get("pic"));
//                    v.addProperty("vod_remarks", vObj.get("state"));
//                    videos.add(v);
//                }
//            }
//        }
//        JsonObject result = new JsonObject();
//        result.addProperty("page", pg);
//        result.addProperty("pagecount", totalPg);
//        result.addProperty("limit", 90);
//        result.addProperty("total", Integer.MAX_VALUE);
//        result.addProperty("list", videos);
//        return result.toString();
//    }
//
//    @Override
//    public String detailContent(List<String> ids) throws Exception {
//        String apiUrl = getApiUrl();
//        String url = getPlayUrlPrefix(apiUrl) + ids.get(0);
//        SpiderDebug.log(url);
//        String json = OkHttp.string(url, getHeaders(url));
//        JsonObject obj = new JsonObject(json);
//        JsonObject result = new JsonObject();
//        JsonObject vod = new JsonObject();
//        genPlayList(apiUrl, obj, json, vod, ids.get(0));
//        JsonArray list = new JsonArray();
//        list.add(vod);
//        result.add("list", list);
//        return result.toString();
//    }
//
//    @Override
//    public String searchContent(String key, boolean quick) throws Exception {
//        String apiUrl = getApiUrl();
//        String url = getSearchUrl(apiUrl, URLEncoder.encode(key));
//        String json = OkHttp.string(url, getHeaders(url));
//        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
//        JsonArray JsonArray = null;
//        JsonArray videos = new JsonArray();
//        if (obj.has("list") && obj.get("list") instanceof JsonArray) {
//            JsonArray = obj.getAsJsonArray("list");
//        } else if (obj.has("data") && obj.get("data") instanceof JsonObject && obj.getAsJsonObject("data").has("list") && obj.getAsJsonObject("data").get("list") instanceof JsonArray) {
//            JsonArray = obj.getAsJsonObject("data").getAsJsonArray("list");
//        } else if (obj.has("data") && obj.get("data") instanceof JsonArray) {
//            JsonArray = obj.getAsJsonArray("data");
//        }
//        if (JsonArray != null) {
//            for (int i = 0; i < JsonArray.size(); i++) {
//                JsonObject vObj = JsonArray.getAsJsonObject(i);
//                if (vObj.has("vod_id")) {
//                    JsonObject v = new JsonObject();
//                    v.addProperty("vod_id", vObj.get("vod_id").getAsString());
//                    v.addProperty("vod_name", vObj.get("vod_name").getAsString());
//                    v.addProperty("vod_pic", vObj.get("vod_pic").getAsString());
//                    v.addProperty("vod_remarks", vObj.get("vod_remarks").getAsString());
//                    videos.add(v);
//                } else {
//                    JsonObject v = new JsonObject();
//                    v.addProperty("vod_id", vObj.get("nextlink").getAsString());
//                    v.addProperty("vod_name", vObj.get("title").getAsString());
//                    v.addProperty("vod_pic", vObj.get("pic").getAsString());
//                    v.addProperty("vod_remarks", vObj.get("state").getAsString());
//                    videos.add(v);
//                }
//            }
//        }
//        JsonObject result = new JsonObject();
//        result.add("list", videos);
//        return result.toString();
//    }
//
//    @Override
//    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
//        if (flag.contains("fanqie") && Utils.isVideoFormat(id)) {
//            JsonObject result = new JsonObject();
//            result.addProperty("parse", 0);
//            result.addProperty("playUrl", "");
//            result.addProperty("url", id);
//            return result.toString();
//        }
//        ArrayList<String> parseUrls = parseUrlMap.get(flag);
//        if (parseUrls == null) parseUrls = new ArrayList<>();
//        if (!parseUrls.isEmpty()) {
//            JsonObject result = getFinalVideo(flag, parseUrls, id);
//            if (result != null) return result.toString();
//        }
//        if (Utils.isVideoFormat(id)) {
//            JsonObject result = new JsonObject();
//            result.addProperty("parse", 0);
//            result.addProperty("playUrl", "");
//            result.addProperty("url", id);
//            return result.toString();
//        } else {
//            JsonObject result = new JsonObject();
//            result.addProperty("parse", 1);
//            result.addProperty("jx", "1");
//            result.addProperty("url", id);
//            return result.toString();
//        }
//    }
//
//    private void findJsonArray(JsonObject obj, String match, ArrayList<JsonArray> result) {
//        Iterator<String> keys = obj.keySet().iterator();
//        while (keys.hasNext()) {
//            String k = keys.next();
//            try {
//                Object o = obj.get(k);
//                if (k.equals(match) && o instanceof JsonArray) result.add((JsonArray) o);
//                if (o instanceof JsonObject) {
//                    findJsonArray((JsonObject) o, match, result);
//                } else if (o instanceof JsonArray) {
//                    JsonArray array = (JsonArray) o;
//                    for (int i = 0; i < array.size(); i++) {
//                        findJsonArray(array.getAsJsonObject(i), match, result);
//                    }
//                }
//            } catch (JsonIOException e) {
//                SpiderDebug.log(e);
//            }
//        }
//    }
//
//    private String jsonArr2Str(JsonArray array) {
//        try {
//            ArrayList<String> strings = new ArrayList<>();
//            for (int i = 0; i < array.size(); i++) {
//                strings.add(array.get(i).getAsString());
//            }
//            return StringUtils.join( strings,",");
//        } catch (JsonIOException e) {
//            return "";
//        }
//    }
//
//    private HashMap<String, String> getHeaders(String URL) {
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("User-Agent", UA(URL));
//        return headers;
//    }
//
//    // M 扩展方法
//    // ######重组搜索
//    private String getSearchUrl(String URL, String KEY) {
//        if (URL.contains(".vod")) {
//            if (URL.contains("iopenyun.com")) {
//                return URL + "/list?wd=" + KEY + "&page=";
//            } else {
//                return URL + "?wd=" + KEY + "&page=";
//            }
//        } else if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//            return URL + "search?text=" + KEY + "&pg=";
//        } else if (urlPattern1.matcher(URL).find()) {
//            if (URL.contains("esellauto") || URL.contains("1.14.63.101") || URL.contains("zjys") || URL.contains("dcd") || URL.contains("lxue") || URL.contains("weetai.cn") || URL.contains("haokanju1") || URL.contains("fit:8") || URL.contains("zjj.life") || URL.contains("love9989") || URL.contains("8d8q") || URL.contains("lk.pxun") || URL.contains("hgyx") || URL.contains("521x5") || URL.contains("lxyyy") || URL.contains("0818tv") || URL.contains("diyoui") || URL.contains("diliktv") || URL.contains("ppzhu") || URL.contains("aitesucai") || URL.contains("zz.ci") || URL.contains("chxjon") || URL.contains("watchmi") || URL.contains("vipbp") || URL.contains("bhtv") || URL.contains("xfykl")) {
//                return URL + "?ac=list&" + "wd=" + KEY + "&page=";
//            } else {
//                return URL + "?ac=list&" + "zm=" + KEY + "&page=";
//            }
//        }
//        return "";
//    }
//
//    // ######UA
//    private static final Pattern urlPattern1 = Pattern.compile("api\\.php/.*?/vod");
//    private static final Pattern urlPattern2 = Pattern.compile("api\\.php/.+?\\.vod");
//    private static final Pattern parsePattern = Pattern.compile("/.+\\?.+=");
//    private static final Pattern parsePattern1 = Pattern.compile(".*(url|v|vid|php\\?id)=");
//    private static final Pattern parsePattern2 = Pattern.compile("https?://[^/]*");
//
//    protected static final Pattern[] htmlVideoKeyMatch = new Pattern[]{Pattern.compile("player=new"), Pattern.compile("<div id=\"video\""), Pattern.compile("<div id=\"[^\"]*?player\""), Pattern.compile("//视频链接"), Pattern.compile("HlsJsPlayer\\("), Pattern.compile("<iframe[\\s\\S]*?src=\"[^\"]+?\""), Pattern.compile("<video[\\s\\S]*?src=\"[^\"]+?\"")};
//
//    private String UA(String URL) {
//        if (URL.contains("vod.9e03.com")) {
//            return "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Mobile Safari/537.36";
//        } else if (URL.contains("api.php/app") || URL.contains("xgapp") || URL.contains("freekan")) {
//            return "Dart/2.14 (dart:io)";
//        } else if (URL.contains("zsb") || URL.contains("fkxs") || URL.contains("xays") || URL.contains("xcys") || URL.contains("szys") || URL.contains("dxys") || URL.contains("ytys") || URL.contains("qnys")) {
//            return "Dart/2.15 (dart:io)";
//        } else if (URL.contains(".vod")) {
//            return "okhttp/4.1.0";
//        } else {
//            return "Dalvik/2.1.0";
//        }
//    }
//
//    // ######获取分类地址
//    String getCateUrl(String URL) {
//        if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//            return URL + "nav?token=";
//        } else if (URL.contains(".vod")) {
//            if (URL.contains("iopenyun.com")) {
//                return URL + "/list?type";
//            } else {
//                return URL + "/types";
//            }
//        } else {
//            return "";
//        }
//    }
//
//    // ######分类筛选前缀地址
//    String getCateFilterUrlPrefix(String URL) {
//        if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//            if (URL.contains("dijiaxia")) {
//                URL = "http://www.dijiaxia.com/api.php/app/";
//                return URL + "video?tid=";
//            } else {
//                return URL + "video?tid=";
//            }
//        } else if (URL.contains(".vod")) {
//            if (URL.contains("tv.bulei.cc")) {
//                return "https://tv.bulei.cc/api2.php/v1.vod?type=";
//            }
//            if (URL.contains("iopenyun")) {
//                return URL + "/list?type=";
//            } else {
//                return URL + "?type=";
//            }
//        } else {
//            return URL + "?ac=list&class=";
//        }
//    }
//
//    // ######分类筛选后缀地址
//    String getCateFilterUrlSuffix(String URL) {
//        if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//            return "&class=筛选class&area=筛选area&lang=筛选lang&year=筛选year&limit=18&pg=#PN#";
//        } else if (URL.contains(".vod")) {
//            return "&class=筛选class&area=筛选area&lang=筛选lang&year=筛选year&by=排序&limit=18&page=#PN#";
//        } else {
//            return "&page=#PN#&area=筛选area&type=筛选class&start=筛选year";
//        }
//    }
//
//    // ######筛选内容
//    String getFilterTypes(String URL, JsonObject typeExtend) {
//        String str = "";
//        if (typeExtend != null) {
//            Iterator<String> typeExtendKeys = typeExtend.keys();
//            while (typeExtendKeys.hasNext()) {
//                String key = typeExtendKeys.next();
//                if (key.equals("class") || key.equals("area") || key.equals("lang") || key.equals("year")) {
//                    try {
//                        str = str + "筛选" + key + "+全部=+" + typeExtend.get(key).replace(",", "+") + "\n";
//                    } catch (JSONException e) {
//                    }
//                }
//            }
//        }
//        if (URL.contains(".vod")) {
//            str += "\n" + "排序+全部=+最新=time+最热=hits+评分=score";
//        } else if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//        } else {
//            str = "分类+全部=+电影=movie+连续剧=tvplay+综艺=tvshow+动漫=comic+4K=movie_4k+体育=tiyu\n筛选class+全部=+喜剧+爱情+恐怖+动作+科幻+剧情+战争+警匪+犯罪+动画+奇幻+武侠+冒险+枪战+恐怖+悬疑+惊悚+经典+青春+文艺+微电影+古装+历史+运动+农村+惊悚+惊悚+伦理+情色+福利+三级+儿童+网络电影\n筛选area+全部=+大陆+香港+台湾+美国+英国+法国+日本+韩国+德国+泰国+印度+西班牙+加拿大+其他\n筛选year+全部=+2023+2022+2021+2020+2019+2018+2017+2016+2015+2014+2013+2012+2011+2010+2009+2008+2007+2006+2005+2004+2003+2002+2001+2000";
//        }
//        return str;
//    }
//
//    // ######推荐地址
//    String getRecommendUrl(String URL) {
//        if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//            return URL + "index_video?token=";
//        } else if (URL.contains(".vod")) {
//            return URL + "/vodPhbAll";
//        } else {
//            return "";
//        }
//    }
//
//    // ######播放器前缀地址
//    String getPlayUrlPrefix(String URL) {
//        if (URL.contains("api.php/app") || URL.contains("xgapp")) {
//            if (URL.contains("dijiaxia")) {
//                URL = "https://www.dijiaxia.com/api.php/app/";
//                return URL + "video_detail?id=";
//            } else if (URL.contains("1010dy")) {
//                URL = "http://www.1010dy.cc/api.php/app/";
//                return URL + "video_detail?id=";
//            } else {
//                return URL + "video_detail?id=";
//            }
//        } else if (URL.contains(".vod")) {
//            if (URL.contains("iopenyun")) {
//                return URL + "/detailID?vod_id=";
//            } else {
//                return URL + "/detail?vod_id=";
//            }
//        } else {
//            return "";
//        }
//    }
//
//    // ######选集
//    protected final HashMap<String, ArrayList<String>> parseUrlMap = new HashMap<>();
//
//    private void genPlayList(String URL, JsonObject object, String json, JsonObject vod, String vid) throws JSONException {
//        ArrayList<String> playUrls = new ArrayList<>();
//        ArrayList<String> playFlags = new ArrayList<>();
//        if (URL.contains("api.php/app/")) {
//            JsonObject data = object.getAsJsonObject("data");
//            vod.addProperty("vod_id", data.optString("vod_id", vid));
//            vod.addProperty("vod_name", data.get("vod_name"));
//            vod.addProperty("vod_pic", data.get("vod_pic"));
//            vod.addProperty("type_name", data.optString("vod_class"));
//            vod.addProperty("vod_year", data.optString("vod_year"));
//            vod.addProperty("vod_area", data.optString("vod_area"));
//            vod.addProperty("vod_remarks", data.optString("vod_remarks"));
//            vod.addProperty("vod_actor", data.optString("vod_actor"));
//            vod.addProperty("vod_director", data.optString("vod_director"));
//            vod.addProperty("vod_content", data.optString("vod_content"));
//            JsonArray vodUrlWithPlayer = data.getAsJsonArray("vod_url_with_player");
//            for (int i = 0; i < vodUrlWithPlayer.length(); i++) {
//                JsonObject from = vodUrlWithPlayer.getAsJsonObject(i);
//                String flag = from.optString("code").trim();
//                if (flag.isEmpty()) flag = from.get("name").trim();
//                playFlags.add(flag);
//                playUrls.add(from.get("url"));
//                String purl = from.optString("parse_api").trim();
//                ArrayList<String> parseUrls = parseUrlMap.get(flag);
//                if (parseUrls == null) {
//                    parseUrls = new ArrayList<>();
//                    parseUrlMap.addProperty(flag, parseUrls);
//                }
//                if (!purl.isEmpty() && !parseUrls.contains(purl)) parseUrls.add(purl);
//            }
//        } else if (URL.contains("xgapp")) {
//            JsonObject data = object.getAsJsonObject("data").getAsJsonObject("vod_info");
//            vod.addProperty("vod_id", data.optString("vod_id", vid));
//            vod.addProperty("vod_name", data.get("vod_name"));
//            vod.addProperty("vod_pic", data.get("vod_pic"));
//            vod.addProperty("type_name", data.optString("vod_class"));
//            vod.addProperty("vod_year", data.optString("vod_year"));
//            vod.addProperty("vod_area", data.optString("vod_area"));
//            vod.addProperty("vod_remarks", data.optString("vod_remarks"));
//            vod.addProperty("vod_actor", data.optString("vod_actor"));
//            vod.addProperty("vod_director", data.optString("vod_director"));
//            vod.addProperty("vod_content", data.optString("vod_content"));
//            JsonArray vodUrlWithPlayer = data.getAsJsonArray("vod_url_with_player");
//            for (int i = 0; i < vodUrlWithPlayer.length(); i++) {
//                JsonObject from = vodUrlWithPlayer.getAsJsonObject(i);
//                String flag = from.optString("code").trim();
//                if (flag.isEmpty()) flag = from.get("name").trim();
//                playFlags.add(flag);
//                playUrls.add(from.get("url"));
//                String purl = from.optString("parse_api").trim();
//                ArrayList<String> parseUrls = parseUrlMap.get(flag);
//                if (parseUrls == null) {
//                    parseUrls = new ArrayList<>();
//                    parseUrlMap.addProperty(flag, parseUrls);
//                }
//                if (!purl.isEmpty() && !parseUrls.contains(purl)) parseUrls.add(purl);
//            }
//        } else if (/*urlPattern2.matcher(URL).find()*/URL.contains(".vod")) {
//            JsonObject data = object.getAsJsonObject("data");
//            vod.addProperty("vod_id", data.optString("vod_id", vid));
//            vod.addProperty("vod_name", data.get("vod_name"));
//            vod.addProperty("vod_pic", data.get("vod_pic"));
//            vod.addProperty("type_name", data.optString("vod_class"));
//            vod.addProperty("vod_year", data.optString("vod_year"));
//            vod.addProperty("vod_area", data.optString("vod_area"));
//            vod.addProperty("vod_remarks", data.optString("vod_remarks"));
//            vod.addProperty("vod_actor", data.optString("vod_actor"));
//            vod.addProperty("vod_director", data.optString("vod_director"));
//            vod.addProperty("vod_content", data.optString("vod_content"));
//            JsonArray vodUrlWithPlayer = data.getAsJsonArray("vod_play_list");
//            for (int i = 0; i < vodUrlWithPlayer.length(); i++) {
//                JsonObject from = vodUrlWithPlayer.getAsJsonObject(i);
//                String flag = from.getAsJsonObject("player_info").optString("from").trim();
//                if (flag.isEmpty()) flag = from.getAsJsonObject("player_info").optString("show").trim();
//                playFlags.add(flag);
//                playUrls.add(from.get("url"));
//                try {
//                    ArrayList<String> parses = new ArrayList<>();
//                    String[] parse1 = from.getAsJsonObject("player_info").optString("parse").split(",");
//                    String[] parse2 = from.getAsJsonObject("player_info").optString("parse2").split(",");
//                    parses.addAll(Arrays.asList(parse1));
//                    parses.addAll(Arrays.asList(parse2));
//                    ArrayList<String> parseUrls = parseUrlMap.get(flag);
//                    if (parseUrls == null) {
//                        parseUrls = new ArrayList<>();
//                        parseUrlMap.addProperty(flag, parseUrls);
//                    }
//                    for (String purl : parses) {
//                        if (purl.contains("http")) {
//                            Matcher matcher = parsePattern1.matcher(purl);
//                            if (matcher.find()) {
//                                purl = matcher.group(0);
//                            }
//                        } else if (purl.contains("//")) {
//                            Matcher matcher = parsePattern1.matcher(purl);
//                            if (matcher.find()) {
//                                purl = "http:" + matcher.group(0);
//                            }
//                        } else {
//                            Matcher matcher = parsePattern2.matcher(URL);
//                            if (matcher.find()) {
//                                Matcher matcher1 = parsePattern1.matcher(URL);
//                                if (matcher1.find()) {
//                                    purl = matcher.group(0) + matcher1.group(0);
//                                }
//                            }
//                        }
//                        purl = purl.replace("..", ".").trim();
//                        if (!purl.isEmpty() && !parseUrls.contains(purl)) parseUrls.add(purl);
//                    }
//                } catch (Exception e) {
//                    SpiderDebug.log(e);
//                }
//            }
//        } else if (urlPattern1.matcher(URL).find()) {
//            JsonObject data = object;
//            vod.addProperty("vod_id", data.optString("vod_id", vid));
//            vod.addProperty("vod_name", data.get("title"));
//            vod.addProperty("vod_pic", data.get("img_url"));
//            vod.addProperty("type_name", jsonArr2Str(data.optJsonArray("type")));
//            vod.addProperty("vod_year", data.optString("pubtime"));
//            vod.addProperty("vod_area", jsonArr2Str(data.optJsonArray("area")));
//            vod.addProperty("vod_remarks", data.optString("trunk"));
//            vod.addProperty("vod_actor", jsonArr2Str(data.optJsonArray("actor")));
//            vod.addProperty("vod_director", jsonArr2Str(data.optJsonArray("director")));
//            vod.addProperty("vod_content", data.optString("intro"));
//            JsonObject playList = data.getAsJsonObject("videolist");
//            Iterator<String> playListKeys = playList.keySet().iterator();
//            while (playListKeys.hasNext()) {
//                String flag = playListKeys.next();
//                ArrayList<String> parseUrls = parseUrlMap.get(flag);
//                if (parseUrls == null) {
//                    parseUrls = new ArrayList<>();
//                    parseUrlMap.add(flag, parseUrls);
//                }
//                JsonArray playListUrls = playList.getAsJsonArray(flag);
//                ArrayList<String> urls = new ArrayList<>();
//                for (int j = 0; j < playListUrls.size(); j++) {
//                    JsonObject urlObj = playListUrls.get(j).getAsJsonObject();
//                    String url = urlObj.get("url").getAsString();
//                    if (url.contains("url=")) {
//                        int spIdx = url.indexOf("url=") + 4;
//                        String pUrl = url.substring(0, spIdx).trim();
//                        if (!pUrl.isEmpty() && !parseUrls.contains(pUrl)) parseUrls.add(pUrl);
//                        urls.add(urlObj.get("title") + "$" + url.substring(spIdx).trim());
//                    } else {
//                        urls.add(urlObj.get("title") + "$" + url);
//                    }
//                }
//                playFlags.add(flag);
//                playUrls.add(StringUtils.join( urls,"#"));
//            }
//        }
//        vod.addProperty("vod_play_from", StringUtils.join(playFlags,"$$$"));
//        vod.addProperty("vod_play_url", StringUtils.join(playUrls,"$$$"));
//    }
//
//    // ######视频地址
//    protected JsonObject getFinalVideo(String flag, ArrayList<String> parseUrls, String url){
//        String htmlPlayUrl = "";
//        for (String parseUrl : parseUrls) {
//            if (parseUrl.isEmpty() || parseUrl.equals("null")) continue;
//            String playUrl = parseUrl + url;
//            String content = OkHttp.string(playUrl);
//            JsonObject tryJson = null;
//            try {
//                tryJson = jsonParse(url, content);
//            } catch (Throwable th) {
//
//            }
//            if (tryJson != null && tryJson.has("url") && tryJson.has("header")) {
//                tryJson.addProperty("header", tryJson.getAsJsonObject("header").toString());
//                return tryJson;
//            }
//            if (content.contains("<html")) {
//                boolean sniffer = false;
//                for (Pattern p : htmlVideoKeyMatch) {
//                    if (p.matcher(content).find()) {
//                        sniffer = true;
//                        break;
//                    }
//                }
//                if (sniffer) {
//                    htmlPlayUrl = parseUrl;
//                }
//            }
//        }
//        if (!htmlPlayUrl.isEmpty()) {
//            JsonObject result = new JsonObject();
//            result.addProperty("parse", 1);
//            result.addProperty("playUrl", htmlPlayUrl);
//            result.addProperty("url", url);
//            return result;
//        }
//        return null;
//    }
//
//    @Override
//    public boolean manualVideoCheck() {
//        return true;
//    }
//
//    @Override
//    public boolean isVideoFormat(String url) {
//        return Utils.isVideoFormat(url);
//    }
//
//    private String getApiUrl() {
//        if (extInfos == null || extInfos.length < 1) return "";
//        return extInfos[0].trim();
//    }
//
//    private String[] extInfos = null;
//
//    public static JsonObject jsonParse(String inaddProperty, String json) {
//        //处理解析接口返回的报文，如果返回的报文中包含header信息，就加到返回值中
//        JsonObject jsonPlayData = JsonParser.parseString(json).getAsJsonObject();
//        if (jsonPlayData.has("data") && jsonPlayData.get("data") instanceof JsonObject && !jsonPlayData.has("url")) {
//            jsonPlayData = jsonPlayData.getAsJsonObject("data");
//        }
//        String url = jsonPlayData.get("url").getAsString();
//        if (url.startsWith("//")) {
//            url = "https:" + url;
//        }
//        if (!url.trim().startsWith("http")) {
//            return null;
//        }
//        if (url.equals(inaddProperty)) {
//            if (Utils.isVip(url) || !Utils.isVideoFormat(url)) {
//                return null;
//            }
//        }
//        if (Utils.isBlackVodUrl(url)) {
//            return null;
//        }
//        JsonObject headers = new JsonObject();
//        if (jsonPlayData.has("header")) {
//            headers = jsonPlayData.getAsJsonObject("header");
//        } else if (jsonPlayData.has("Header")) {
//            headers = jsonPlayData.getAsJsonObject("Header");
//        } else if (jsonPlayData.has("headers")) {
//            headers = jsonPlayData.getAsJsonObject("headers");
//        } else if (jsonPlayData.has("Headers")) {
//            headers = jsonPlayData.getAsJsonObject("Headers");
//        }
//        String ua = "";
//        if (jsonPlayData.has("user-agent")) {
//            ua = jsonPlayData.get("user-agent").getAsString();
//        } else if (jsonPlayData.has("User-Agent")) {
//            ua = jsonPlayData.get("User-Agent").getAsString();
//        }
//        if (ua.trim().length() > 0) {
//            headers.addProperty("User-Agent", " " + ua);
//        }
//        String referer = "";
//        if (jsonPlayData.has("referer")) {
//            referer = jsonPlayData.get("referer").getAsString();
//        } else if (jsonPlayData.has("Referer")) {
//            referer = jsonPlayData.get("Referer").getAsString();
//        }
//        if (referer.trim().length() > 0) {
//            headers.addProperty("Referer", " " + referer);
//        }
//
//        headers = fixJsonVodHeader(headers, inaddProperty, url);
//        JsonObject taskResult = new JsonObject();
//        taskResult.add("header", headers);
//        taskResult.addProperty("url", url);
//        taskResult.addProperty("parse", "0");
//        return taskResult;
//    }
//
//    public static JsonObject fixJsonVodHeader(JsonObject headers, String inaddProperty, String url){
//        if (headers == null) headers = new JsonObject();
//        if (inaddProperty.contains("www.mgtv.com")) {
//            headers.addProperty("Referer", " ");
//            headers.addProperty("User-Agent", " Mozilla/5.0");
//        } else if (url.contains("titan.mgtv")) {
//            headers.addProperty("Referer", " ");
//            headers.addProperty("User-Agent", " Mozilla/5.0");
//        } else if (inaddProperty.contains("bilibili")) {
//            headers.addProperty("Referer", " https://www.bilibili.com/");
//            headers.addProperty("User-Agent", " " + Utils.CHROME);
//        }
//        return headers;
//    }
//}