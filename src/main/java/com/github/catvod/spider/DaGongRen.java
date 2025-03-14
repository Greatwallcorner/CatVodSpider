package com.github.catvod.spider;

import cn.hutool.core.net.URLEncodeUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaGongRen extends Spider {

    private static final String siteUrl = "https://dagongren1.com";
    private static final String cateUrl = siteUrl + "/list/";
    private static final String detailUrl = siteUrl + "/play/";
    private static final String playUrl = siteUrl + "/play/";
    private static final String searchUrl = siteUrl + "/search--------------.html?wd=";

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        return headers;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        String[] typeIdList = {"dianying", "dianshiju", "zongyi", "dongman", "jilupian", "lunlipian"};
        String[] typeNameList = {"电影", "连续剧", "综艺", "动漫", "纪录片", "福利"};
        for (int i = 0; i < typeNameList.length; i++) {
            classes.add(new Class(typeIdList[i], typeNameList[i]));
        }
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeaders()));
        for (Element element : doc.select("a.vodlist_thumb")) {
            try {
                String pic = element.attr("data-original");
                String url = element.attr("href");
                String name = element.attr("title");
                if (!pic.startsWith("http")) {
                    pic = siteUrl + pic;
                }
                String id = url.split("/")[2];
                list.add(new Vod(id, name, pic));
            } catch (Exception ignored) {
            }
        }
        return Result.string(classes, list);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        tid = "/show-" + tid + "--------" + pg + "---.html";
        String target = siteUrl + tid;
        Document doc = Jsoup.parse(OkHttp.string(target, getHeaders()));
        for (Element element : doc.select("a.vodlist_thumb")) {
            try {
                String pic = element.attr("data-original");
                String url = element.attr("href");
                String name = element.attr("title");
                if (!pic.startsWith("http")) {
                    pic = siteUrl + pic;
                }
                String id = url.split("/")[2];
                list.add(new Vod(id, name, pic));
            } catch (Exception e) {
            }
        }

        int total = (Integer.parseInt(pg) + 1) * 20;
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(pg) + 1, 20, total).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Document doc = Jsoup.parse(OkHttp.string(detailUrl.concat(ids.get(0)), getHeaders()));
        String name = doc.select("h2.title.margin_0").text();
        String pic = doc.select("div.play_vlist_thumb").get(0).attr("data-original");
        // 播放源
        Elements tabs = doc.select("li.tab-play");
        Elements list = doc.select("ul.content_playlist");
        StringBuilder PlayFrom = new StringBuilder();
        StringBuilder PlayUrl = new StringBuilder();
        for (int i = 0; i < tabs.size(); i++) {
            String tabName = tabs.get(i).text();
            if (PlayFrom.length() > 0) {
                PlayFrom.append("$$$").append(tabName);
            } else {
                PlayFrom.append(tabName);
            }
            Elements li = list.get(i).select("a");
            StringBuilder liUrl = new StringBuilder();
            for (int i1 = 0; i1 < li.size(); i1++) {
                if (liUrl.length() > 0) {
                    liUrl.append("#").append(li.get(i1).text()).append("$").append(li.get(i1).attr("href").replace("/play/", ""));
                } else {
                    liUrl.append(li.get(i1).text()).append("$").append(li.get(i1).attr("href").replace("/play/", ""));
                }
            }
            if (PlayUrl.length() > 0) {
                PlayUrl.append("$$$").append(liUrl);
            } else {
                PlayUrl.append(liUrl);
            }
        }

        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(siteUrl + pic);
        vod.setVodName(name);
        vod.setVodPlayFrom(PlayFrom.toString());
        vod.setVodPlayUrl(PlayUrl.toString());
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(searchUrl.concat(URLEncodeUtil.encode(key, StandardCharsets.UTF_8)), getHeaders()));
        for (Element element : doc.select("div.searchlist_img")) {
            try {
                String pic = element.select("a").attr("data-original");
                String url = element.select("a").attr("href");
                String name = element.select("a").attr("title");
                if (!pic.startsWith("http")) {
                    pic = siteUrl + pic;
                }
                String id = url.replace("/video/", "").replace(".html", "-1-1.html");
                list.add(new Vod(id, name, pic));
            } catch (Exception e) {
            }
        }
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String target = playUrl.concat(id);
        Document doc = Jsoup.parse(OkHttp.string(target));
        String regex = "\"url\\\":\\\"(.*?)\\\",\\\"url_next\\\":";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(doc.html());
        String url = "";
        if (matcher.find()) {
            url = URLDecoder.decode(matcher.group(1), "UTF-8").split("&")[0];
        }
        return Result.get().url(url).header(getHeaders()).string();
    }
}