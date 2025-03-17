package com.github.catvod.spider;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Util;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FongMi
 */
public class Dm84 extends Spider {

    private static final String siteUrl = Util.base64Decode("aHR0cHM6Ly9kbTg0LnR2");
    private static final String playUrl = Util.base64Decode("aHR0cHM6Ly9oaGp4LmhocGxheWVyLmNvbQ==");

    private static final Map<String, String> decodeMapping = Json.parseSafe(Util.base64Decode("eyAiME9vMG8wTzAiOiAiYSIsICIxTzBiTzAwMSI6ICJiIiwgIjJPb0NjTzIiOiAiYyIsICIzTzBkTzBPMyI6ICJkIiwgIjRPb0VlTzQiOiAiZSIsICI1TzBmTzBPNSI6ICJmIiwgIjZPb0dnTzYiOiAiZyIsICI3TzBoTzBPNyI6ICJoIiwgIjhPb0lpTzgiOiAiaSIsICI5TzBqTzBPOSI6ICJqIiwgIjBPb0trTzAiOiAiayIsICIxTzBsTzBPMSI6ICJsIiwgIjJPb01tTzIiOiAibSIsICIzTzBuTzBPMyI6ICJuIiwgIjRPb09vTzQiOiAibyIsICI1TzBwTzBPNSI6ICJwIiwgIjZPb1FxTzYiOiAicSIsICI3TzByTzBPNyI6ICJyIiwgIjhPb1NzTzgiOiAicyIsICI5TzB0TzBPOSI6ICJ0IiwgIjBPb1V1TzAiOiAidSIsICIxTzB2TzBPMSI6ICJ2IiwgIjJPb1d3TzIiOiAidyIsICIzTzB4TzBPMyI6ICJ4IiwgIjRPb1l5TzQiOiAieSIsICI1TzB6TzBPNSI6ICJ6IiwgIjBPb0FBTzAiOiAiQSIsICIxTzBCQk8xIjogIkIiLCAiMk9vQ0NPMiI6ICJDIiwgIjNPMERETzMiOiAiRCIsICI0T29FRU80IjogIkUiLCAiNU8wRkZPNSI6ICJGIiwgIjZPb0dHTzYiOiAiRyIsICI3TzBISE83IjogIkgiLCAiOE9vSUlPOCI6ICJJIiwgIjlPMEpKTzkiOiAiSiIsICIwT29LS08wIjogIksiLCAiMU8wTExPMSI6ICJMIiwgIjJPb01NTzIiOiAiTSIsICIzTzBOTk8zIjogIk4iLCAiNE9vT09PNCI6ICJPIiwgIjVPMFBQTzUiOiAiUCIsICI2T29RUU82IjogIlEiLCAiN08wUlJPNyI6ICJSIiwgIjhPb1NTTzgiOiAiUyIsICI5TzBUVE85IjogIlQiLCAiME9vVU8wIjogIlUiLCAiMU8wVlZPMSI6ICJWIiwgIjJPb1dXTzIiOiAiVyIsICIzTzBYWE8zIjogIlgiLCAiNE9vWVlPNCI6ICJZIiwgIjVPMFpaTzUiOiAiWiJ9"), Map.class);

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        headers.put("Accept", Util.ACCEPT);
        return headers;
    }

    private Filter getFilter(String name, String key, List<String> texts) {
        List<Filter.Value> values = new ArrayList<>();
        for (String text : texts) {
            if (text.isEmpty()) continue;
            String n = text.replace("按", "");
            String v = key.equals("by") ? replaceBy(text) : text;
            values.add(new Filter.Value(n, v));
        }
        return new Filter(key, name, values);
    }

    private String replaceBy(String text) {
        return text.replace("按时间", "time").replace("按人气", "hits").replace("按评分", "score");
    }

    @Override
    public String homeContent(boolean filter) {
        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeaders()));
        for (Element element : doc.select("ul.nav_row > li > a")) {
            if (element.attr("href").startsWith("/list")) {
                String id = element.attr("href").split("-")[1].substring(0, 1);
                String name = element.text().substring(0, 2);
                classes.add(new Class(id, name));
            }
        }
        for (Class item : classes) {
            doc = Jsoup.parse(OkHttp.string(siteUrl + "/list-" + item.getTypeId() + ".html", getHeaders()));
            Elements elements = doc.select("ul.list_filter > li > div");
            List<Filter> array = new ArrayList<>();
            array.add(getFilter("類型", "type", elements.get(0).select("a").eachText()));
            array.add(getFilter("時間", "year", elements.get(1).select("a").eachText()));
            array.add(getFilter("排序", "by", elements.get(2).select("a").eachText()));
            filters.put(item.getTypeId(), array);
        }
        for (Element element : doc.select("div.item")) {
            String img = element.select("a.cover").attr("data-bg");
            String url = element.select("a.title").attr("href");
            String name = element.select("a.title").text();
            String remark = element.select("span.desc").text();
            String id = url.split("/")[2];
            list.add(new Vod(id, name, img, remark));
        }
        return Result.string(classes, list, filters);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        List<Vod> list = new ArrayList<>();
        if (extend.get("type") == null) extend.put("type", "");
        if (extend.get("year") == null) extend.put("year", "");
        if (extend.get("by") == null) extend.put("by", "time");
        String by = extend.get("by");
        String type = URLEncoder.encode(extend.get("type"));
        String year = extend.get("year");
        String target = siteUrl + String.format("/show-%s--%s-%s--%s-%s.html", tid, by, type, year, pg);
        Document doc = Jsoup.parse(OkHttp.string(target, getHeaders()));
        for (Element element : doc.select("div.item")) {
            String img = element.select("a.cover").attr("data-bg");
            String url = element.select("a.title").attr("href");
            String name = element.select("a.title").text();
            String remark = element.select("span.desc").text();
            String id = url.split("/")[2];
            list.add(new Vod(id, name, img, remark));
        }
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) {
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat("/v/").concat(ids.get(0)), getHeaders()));
        String name = doc.select("h1.v_title").text();
        String remarks = doc.select("p.v_desc > span.desc").text();
        String img = doc.select("meta[property=og:image]").attr("content");
        String area = doc.select("meta[name=og:video:area]").attr("content");
        String type = doc.select("meta[name=og:video:class]").attr("content");
        String actor = doc.select("meta[name=og:video:actor]").attr("content");
        String content = doc.select("meta[property=og:description]").attr("content");
        String year = doc.select("meta[name=og:video:release_date]").attr("content");
        String director = doc.select("meta[name=og:video:director]").attr("content");

        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(img);
        vod.setVodYear(year);
        vod.setVodName(name);
        vod.setVodArea(area);
        vod.setVodActor(actor);
        vod.setVodRemarks(remarks);
        vod.setVodContent(content);
        vod.setVodDirector(director);
        vod.setTypeName(type);

        Map<String, String> sites = new LinkedHashMap<>();
        Elements sources = doc.select("ul.tab_control > li");
        Elements sourceList = doc.select("ul.play_list");
        for (int i = 0; i < sources.size(); i++) {
            Element source = sources.get(i);
            String sourceName = source.text();
            Elements playList = sourceList.get(i).select("a");
            List<String> vodItems = new ArrayList<>();
            for (int j = 0; j < playList.size(); j++) {
                Element e = playList.get(j);
                vodItems.add(e.text() + "$" + e.attr("href"));
            }
            if (vodItems.size() > 0) {
                sites.put(sourceName, Util.stringJoin("#", vodItems));
            }
        }
        if (sites.size() > 0) {
            vod.setVodPlayFrom(Util.stringJoin("$$$", Lists.newArrayList(sites.keySet())));
            vod.setVodPlayUrl(Util.stringJoin("$$$", Lists.newArrayList(sites.values())));
        }
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) {
        List<Vod> list = new ArrayList<>();
        String target = siteUrl.concat("/s----------.html?wd=").concat(key);
        Document doc = Jsoup.parse(OkHttp.string(target, getHeaders()));
        for (Element element : doc.select("div.item")) {
            String img = element.select("a.cover").attr("data-bg");
            String url = element.select("a.title").attr("href");
            String name = element.select("a.title").text();
            String remark = element.select("span.desc").text();
            String id = url.split("/")[2];
            list.add(new Vod(id, name, img, remark));
        }
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat(id), getHeaders()));
        String urlLink = doc.select("iframe").attr("src");
        String string = OkHttp.string(urlLink, getHeaders());
        String url = Util.findByRegex(getvarRegx("url"), string, 1);
        String t = Util.findByRegex(getvarRegx("t"), string, 1);
        String act = Util.findByRegex(getvarRegx("act"), string, 1);
        String play = Util.findByRegex(getvarRegx("play"), string, 1);
        String key = Util.findByRegex("var\\s*key\\s*=\\s*hhh[(\\n\"]*(.*)[\")]+", string, 1);
        String decodeString = decodeString(Base64.decodeStr(key));
        HashMap<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("t", t);
        map.put("act", act);
        map.put("play", play);
        map.put("key", decodeString);

        String post = OkHttp.post(playUrl.concat("/api.php"), map, Util.webHeaders(urlLink)).getBody();
        JsonElement parse = Json.parse(post);
        if((parse.getAsJsonObject().get("code").getAsInt()) != 200){
            SpiderDebug.log("dm84 获取播放链接失败：" + post);
            return Result.error(parse.getAsJsonObject().get("msg").getAsString());
        }
        return Result.get().url(parse.getAsJsonObject().get("url").getAsString()).parse().string();
    }

    private String getvarRegx(String var) {
        return String.format("var\\s*%s\\s*=\\s*[\\n\"]*(.*)[\"]+", var);
    }

    public String decodeString(String decodedString) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < decodedString.length(); i++) {
            String l = String.valueOf(decodedString.charAt(i));

            for (Map.Entry<String, String> entry : decodeMapping.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (i + key.length() <= decodedString.length() &&
                        decodedString.substring(i, i + key.length()).equals(key)) {
                    l = value;
                    i += key.length() - 1;
                    break;
                }
            }
            result.append(l);
        }
        return result.toString();
    }
}
