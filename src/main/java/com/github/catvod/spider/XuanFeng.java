package com.github.catvod.spider;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.util.*;

public class XuanFeng extends Spider {

    private static final String siteUrl = "https://miao101.com";
    private static final String apiUrl = siteUrl + "/api";


    private HashMap<String, String> getHeaders() {
        return Util.webHeaders(siteUrl);
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();

        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeaders()));


        for (Element element : doc.select("#navbarSupportedContent > ul > li ")) {
            if (element.select("a.nav-link").attr("href").startsWith("/")) {
                classes.add(new Class(element.select("a.nav-link").attr("href"), element.text()));
            } else {
                for (Element element1 : element.select("ul > li > a")) {
                    classes.add(new Class(element1.attr("href"), element1.text()));
                }

            }
        }

        for (Element element : doc.select("#container > div.row > div.col-md-2.col-6")) {
            try {
                String pic = element.select("img").attr("src");
                String url = element.select("div.cover-wrap > a").attr("href");
                String name = element.select("div.card-title").text();


                list.add(new Vod(url, name, pic));
            } catch (Exception ignored) {
            }
        }
        return Result.string(classes, list);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        tid = tid.replaceAll("1", "");

        String target = apiUrl + tid + "load.json";
        OkResult result = OkHttp.post(target, Map.of("page", pg), getHeaders());
        if (result.getCode() == 200) {
            String body = result.getBody();
            Map<String, ArrayList<Map<String, Object>>> obj = new HashMap<>();
            obj = Json.parseSafe(body, obj.getClass());
            ArrayList<Map<String, Object>> videos = obj.get("videos");
            for (Map<String, Object> video : videos) {
                list.add(new Vod("/video/" + video.get("ID").toString(), video.get("Title").toString(), video.get("Cover").toString()));
            }

            Integer total = Integer.MAX_VALUE;
            return Result.string(Integer.parseInt(pg), Integer.parseInt(pg) + 1, list.size(), total, list);
        }
        return null;

    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat(ids.get(0)), getHeaders()));
        String name = doc.select("#container > div.row.phone > div.col-md-8 > div:nth-child(3) > div.col-md-8 > h1").text();
        String pic = doc.select("#container > div.row.phone > div.col-md-8 > div.d-flex.mb-3 > div.flex-shrink-0 > img").attr("src");
        String year = doc.select("#container > div.row.phone > div.col-md-8 > div.d-flex.mb-3 > div.flex-grow-1 > p:nth-child(11)").text();
        String desc = doc.select("#container > div.row.phone > div.col-md-8 > div.text-break.ft14").text();
        String jsonStr = Util.findByRegex("JSON.parse\\(\"(.*?)\"\\)", doc.html(), 1);
        Map<String, Object> map = Json.parseSafe(jsonStr.replaceAll("\\\\u0022", "\""), Map.class);
        List<Map<String, Object>> headers = (List<Map<String, Object>>) map.get("headers");
        List<List<String>> clips = (List<List<String>>) map.get("clips");
        // 播放源

        String PlayFrom = "";
        String PlayUrl = "";
        for (int i = 0; i < headers.size(); i++) {
            String tabName = (String) headers.get(i).get("Name");
            if (!"".equals(PlayFrom)) {
                PlayFrom = PlayFrom + "$$$" + tabName;
            } else {
                PlayFrom = PlayFrom + tabName;
            }
            String liUrl = "";
            for (List<String> clip : clips) {
                if (!"".equals(liUrl)) {
                    liUrl = liUrl + "#" + clip.get(0) + "$" + clip.get(1);
                } else {
                    liUrl = liUrl + clip.get(0) + "$" + clip.get(1);
                }
            }

            if (!"".equals(PlayUrl)) {
                PlayUrl = PlayUrl + "$$$" + liUrl;
            } else {
                PlayUrl = PlayUrl + liUrl;
            }
        }

        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(pic);
        vod.setVodYear(year);
        vod.setVodName(name);
        vod.setVodContent(desc);
        vod.setVodPlayFrom(PlayFrom);
        vod.setVodPlayUrl(PlayUrl);
        return Result.string(vod);


    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {


        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat("/search?q=").concat(URLEncoder.encode(key, "UTF-8")), getHeaders()));
        for (Element element : doc.select("#container > div.row > div.col-md-2.col-6")) {
            try {
                String pic = element.select("img").attr("src");
                String url = element.select("div.cover-wrap > a").attr("href");
                String name = element.select("h6").text();
                list.add(new Vod(url, name, pic));
            } catch (Exception ignored) {
            }
        }
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return Result.get().url(id).header(getHeaders()).string();
    }
}