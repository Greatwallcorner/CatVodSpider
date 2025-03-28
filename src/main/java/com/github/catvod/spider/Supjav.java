package com.github.catvod.spider;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Supjav extends Spider {

    private static final String siteUrl = "https://supjav.com/zh/";
    private static final String playUrl = "https://lk1.supremejav.com/";

    private HashMap<String, String> getHeaders() {
        return getHeaders(siteUrl);
    }

    private HashMap<String, String> getHeaders(String referer) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Referer", referer);
        headers.put("User-Agent", "PostmanRuntime/7.36.3");
        headers.put("Host", "supjav.com");
        headers.put("Postman-Token", "33290483-3c8d-413f-a160-0d3aea9e6f95");
        return headers;
    }

    private HashMap<String, String> getTVVideoHeaders(String referer) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Referer", referer);
        headers.put("User-Agent", Util.CHROME);
        return headers;
    }

    @Override
    public String homeContent(boolean filter) {
        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeaders()));
        for (Element element : doc.select("ul.nav > li > a")) {
            String href = element.attr("href");
            if (href.split("/").length < 5) continue;
            String typeId = href.replace(siteUrl, "");
            String typeName = element.text();
            classes.add(new Class(typeId, typeName));
        }
        for (Element element : doc.select("div.post")) {
            String src = element.select("img").attr("src");
            String data = element.select("img").attr("data-original");
            String url = element.select("a").attr("href");
            String name = element.select("a").attr("title");
            String pic = TextUtils.isEmpty(data) ? src : data;
            String id = url.split("/")[4];
            list.add(new Vod(id, name, pic));
        }
        return Result.string(classes, list);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl + tid + "/page/" + pg, getHeaders()));
        for (Element element : doc.select("div.post")) {
            String pic = element.select("img").attr("data-original");
            String url = element.select("a").attr("href");
            String name = element.select("a").attr("title");
            String id = url.split("/")[4];
            list.add(new Vod(id, name, pic));
        }
        return Result.string(list);
    }

    @Override
    public String detailContent(List<String> ids) {
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat(ids.get(0)), getHeaders()));
        String name = doc.select("div.post-meta > img").attr("alt");
        String img = doc.select("div.post-meta > img").attr("src");
        String type = doc.select("p.cat > a").text();
        String director = "", actor = "";
        for (Element p : doc.select("div.cats > p")) {
            if (p.select("span").text().contains("Maker")) {
                director = p.select("a").text();
            }
            if (p.select("span").text().contains("Cast")) {
                actor = p.select("a").text();
            }
        }

        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(img);
        vod.setVodName(name);
        vod.setVodActor(actor);
        vod.setVodDirector(director);
        vod.setTypeName(type);

        Map<String, String> sites = new LinkedHashMap<>();
        Elements sources = doc.select("a.btn-server");
        for (int i = 0; i < sources.size(); i++) {
            Element source = sources.get(i);
            String sourceName = source.text();
            if (sourceName.equals("TV")) continue;
            String sourceUrl = source.attr("data-link");
            sites.put(sourceName, "播放" + "$" + sourceUrl);
        }
        if (sites.size() > 0) {
            vod.setVodPlayFrom(StringUtils.join(sites.keySet(), "$$$"));
            vod.setVodPlayUrl(StringUtils.join(sites.values(), "$$$"));
        }
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) {
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat("?s=").concat(URLEncoder.encode(key)), getHeaders()));
        for (Element element : doc.select("div.post")) {
            String pic = element.select("img").attr("data-original");
            String url = element.select("a").attr("href");
            String name = element.select("a").attr("title");
            String id = url.split("/")[4];
            list.add(new Vod(id, name, pic));
        }
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws URISyntaxException, IOException {
        String redirect = OkHttp.getLocation(playUrl + "supjav.php?c=" + new StringBuilder(id).reverse(), getTVVideoHeaders(playUrl + "supjav.php?l=" + new StringBuilder(id) + "&bg=undefined"));
        switch (flag) {
            case "TV":
                return parseTV(redirect);
            case "ST":
                return parseST(redirect);
            case "FST":
                return parseFST(redirect);
            case "VOE":
                return parseVOE(redirect);
            default:
                return Result.get().url(id).parse().string();
        }
    }

    private String parseVOE(String redirect) {
        String data = OkHttp.string(redirect, getTVVideoHeaders(playUrl));
        return Result.get().url(ReUtil.findAllGroup1("prompt\\(\"Node\",(.*?)\\);", data).get(0).trim().replace("\"", "")).header(getHeaders(redirect)).string();
    }

    private String parseFST(String redirect) {
        String data = OkHttp.string(redirect, getTVVideoHeaders(playUrl));
        return Result.get().url(ReUtil.findAllGroup1("file:\"(.*?)\"}]", data).get(0)).header(getHeaders(redirect)).string();
    }

    private String parseTV(String redirect) throws MalformedURLException {
        String data = OkHttp.string(redirect, getTVVideoHeaders(URLUtil.getHost(new URL(redirect)).toString()));
        return Result.get().url(Util.getVar(data, "urlPlay")).header(getTVVideoHeaders(URLUtil.getHost(new URL(redirect)).toString())).string();
    }

    private String parseST(String redirect) throws IOException {
        String data = OkHttp.string(redirect, getTVVideoHeaders(playUrl));
        String robot = Jsoup.parse(data).getElementById("robotlink").text();
        robot = robot.substring(0, robot.indexOf("&token=") + 7);
        for (String text : data.split("&token=")) {
            if (!text.contains("').substring(")) continue;
            robot = "https:/" + robot + text.split("'")[0] + "&stream=1";
            String url = OkHttp.getLocation(robot, getTVVideoHeaders(redirect));
            return Result.get().url(url).header(getHeaders(redirect)).string();
        }
        return "";
    }

    private String parseDS(String redirect) throws URISyntaxException, IOException {
        String host = "https://" + URIUtils.extractHost(new URI(redirect));
        redirect = host + OkHttp.getLocation(redirect, getTVVideoHeaders(playUrl));
        String data = OkHttp.string(redirect, getHeaders());
        for (String text : data.split("'")) {
            if (!text.startsWith("/pass_md5/")) continue;
            String token = text.split("/")[3];
            String url = OkHttp.string(host + text, getHeaders(redirect));
            url = url + getDSRnd() + "?token=" + token + "&expiry=" + System.currentTimeMillis();
            return Result.get().url(url).header(getHeaders(redirect)).string();
        }
        return "";
    }

    private String getDSRnd() {
        StringBuilder sb = new StringBuilder();
        String t = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int o = 0; o < 10; o++) sb.append(t.charAt((int) Math.floor(Math.random() * t.length())));
        return sb.toString();
    }
}