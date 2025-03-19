package com.github.catvod.spider;

import cn.hutool.core.util.URLUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Liangzi extends Spider {

    private final String siteUrl = "https://lzi888.com";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
//        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        return Util.webHeaders("");
//        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeader()));
        List<String> typeNames = Arrays.asList("电影", "连续剧", "动漫", "综艺", "电影解说", "体育");
        List<String> typeIds = Arrays.asList("1", "2", "3", "4", "39", "40");
        for (int i = 0; i < typeIds.size(); i++) {
            classes.add(new Class(typeIds.get(i), typeNames.get(i)));
            //  filters.put(typeIds.get(i), Arrays.asList(new Filter("filters", "過濾", Arrays.asList(new Filter.Value("全部", ""), new Filter.Value("單人作品", "individual"), new Filter.Value("中文字幕", "chinese-subtitle")))));

        }
        List<Vod> list = getVodList(doc);
        SpiderDebug.log("++++++++++++量子-homeContent" + Json.toJson(list));
        return Result.string(classes, list);
    }

    private @NotNull List<Vod> getVodList(Document doc) {
        List<Vod> list = new ArrayList<>();
        for (Element div : doc.select(".module-item")) {
            String id = siteUrl + div.attr("href");
            String name = div.select(".module-item-pic > img").attr("alt");
            String pic = div.select(".module-item-pic > img").attr("src");
            if (pic.isEmpty()) pic = div.select("img").attr("src");
            String remark = div.select(".module-item-note").text();

            list.add(new Vod(id, name, pic, remark));
        }
        return list;
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String target = siteUrl + "/index.php/vod/show/id/" + tid + "/page/" + pg + ".html";
        //String filters = extend.get("filters");
        String html = OkHttp.string(target);
        Document doc = Jsoup.parse(html);
        List<Vod> list = getVodList(doc);
        String total = "" + Integer.MAX_VALUE;
        for (Element element : doc.select("script")) {
            if (element.data().contains("mac_total")) {
                total = element.data().split("'")[1];
            }
        }

        SpiderDebug.log("++++++++++++量子-categoryContent" + Json.toJson(list));
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(total) / 72 + ((Integer.parseInt(total) % 72) > 0 ? 1 : 0), 72, Integer.parseInt(total)).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
/*        SpiderDebug.log("---------detailContent-ids" + Json.toJson(ids));
        String vodId = ids.get(0);
        String detailUrl = vodId;
        Document doc = Jsoup.parse(OkHttp.string(detailUrl));
        Elements circuits = doc.select(".module-tab-item.tab-item");
        Elements sources = doc.select("[class=scroll-content]");
        StringBuilder vod_play_url = new StringBuilder();
        StringBuilder vod_play_from = new StringBuilder();
        for (int i = 0; i < sources.size(); i++) {
            String spanText = circuits.get(i).select("span").text();
            String smallText = circuits.get(i).select("small").text();
            String playFromText = spanText + "(共" + smallText + "集)";
            vod_play_from.append(playFromText).append("$$$");
            Elements aElementArray = sources.get(i).select("a");
            for (int j = 0; j < aElementArray.size(); j++) {
                Element a = aElementArray.get(j);
                String href = siteUrl + a.attr("href");
                String text = a.text();
                vod_play_url.append(text).append("$").append(href);
                boolean notLastEpisode = j < aElementArray.size() - 1;
                vod_play_url.append(notLastEpisode ? "#" : "$$$");
            }
        }
        String title = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > h1").text();
        String director = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(1) > div > a").text();
        String actor = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(2) > div > a").text();
        String year = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(3) > div").text();
        String vodPic = doc.select("#main > div > div.box.view-heading > div.video-cover > div > div > img").attr("data-src");
        String id = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > a.btn-important.btn-large.shadow-drop.video-info-play").attr("href");
        String playUrl = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > a.btn-important.btn-large.shadow-drop.video-info-play").attr("href");
        String palyDoc = OkHttp.string(siteUrl + playUrl);
        JsonObject vodInfo = Json.parse(ReUtil.findAll("var player_aaaa=(.*?)</script>", palyDoc, 1).get(0)).getAsJsonObject();
        String player = OkHttp.string(vodInfo.get("url").getAsString());
        String playerSource = ReUtil.findAll("var main = \"(.*?)\";", player, 1).get(0);
        URI host = URLUtil.getHost(new URL(vodInfo.get("url").getAsString()));

        SpiderDebug.log("++++++++++++++++++++detailContent-vodInfo" + Json.toJson(vodInfo));
        JsonElement vodData = vodInfo.get("vod_data");
        Vod vod = new Vod();
        vod.setVodDirector(director);
        vod.setVodActor(vodData != null ? vodData.getAsJsonObject().get("vod_actor").getAsString() : "");
        vod.setTypeName(vodData != null ? vodData.getAsJsonObject().get("vod_class").getAsString() : "");
        vod.setVodYear(year);
        vod.setVodId(vodId);
        vod.setVodName(title);
        vod.setVodPic(vodPic);
        vod.setVodPlayUrl(vod_play_url.toString());
        vod.setVodPlayFrom(vod_play_from.toString());
        SpiderDebug.log("++++++++++++++++++++detailContent-vod" + Json.toJson(vod));
        return Result.string(vod);*/
        SpiderDebug.log("++++++++++++量子-detailContent--args" + Json.toJson(ids));
        Document doc = Jsoup.parse(OkHttp.string(ids.get(0), getHeader()));
        Elements circuits = doc.select(".module-tab-item.tab-item");
        Elements sources = doc.select("[class=scroll-content]");
        StringBuilder vod_play_url = new StringBuilder();
        StringBuilder vod_play_from = new StringBuilder();
        for (int i = 0; i < sources.size(); i++) {
            String spanText = circuits.get(i).select("span").text();
            String smallText = circuits.get(i).select("small").text();
            String playFromText = spanText + "(共" + smallText + "集)";
            vod_play_from.append(playFromText).append("$$$");
            Elements aElementArray = sources.get(i).select("a");
            for (int j = 0; j < aElementArray.size(); j++) {
                Element a = aElementArray.get(j);
                String href = siteUrl + a.attr("href");
                String text = a.text();
                vod_play_url.append(text).append("$").append(href);
                boolean notLastEpisode = j < aElementArray.size() - 1;
                vod_play_url.append(notLastEpisode ? "#" : "$$$");
            }
        }
        String title = doc.select(".module-info-heading h1").text();
//        String classifyName = doc.select("div.tag-link a").text();
        String year = doc.select(".module-info-tag-link").eq(1).text();
        String area = doc.select(".module-info-tag-link").eq(2).text();
        Elements select = doc.select(".module-info-item");

        String remark = doc.select("div.title-info span").text();
        String vodPic = doc.select("#main > div > div.box.view-heading > div.video-cover > div > div > img").attr("data-src");

        String director = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(1) > div > a").text();

        String actor = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(2) > div > a").text();

        String brief = "";
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodYear(year);
        vod.setVodName(title);
        vod.setVodArea(area);
        vod.setVodActor(actor);
        vod.setVodPic(vodPic);
        vod.setVodRemarks(remark);
        vod.setVodContent(brief);
        vod.setVodDirector(director);
//        vod.setTypeName(classifyName);
        vod.setVodPlayFrom(vod_play_from.toString());
        vod.setVodPlayUrl(vod_play_url.toString());
        SpiderDebug.log("++++++++++++量子-detailContent" + Json.toJson(vod));
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String searchUrl = siteUrl + "/index.php/vod/search.html?wd=";
        String html = OkHttp.string(searchUrl + key);
        Document document = Jsoup.parse(html);
        List<Vod> list = new ArrayList<>();


        for (Element div : document.select(".module-search-item")) {

            String vodId = siteUrl + div.select(".video-serial").attr("href");
            String name = div.select(".video-info-header > a").attr("title");
            String remark = div.select(".tag-link > a").text();
            String pic = div.select("img").attr("data-src");
            list.add(new Vod(vodId, name, pic, remark));
        }

        SpiderDebug.log("++++++++++++量子-searchContent" + Json.toJson(list));
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String content = OkHttp.string(id, getHeader());
        Matcher matcher = Pattern.compile("player_aaaa=(.*?)</script>").matcher(content);
        String json = matcher.find() ? matcher.group(1) : "";
        JSONObject player = new JSONObject(json);
        String realUrl = player.getString("url");
        SpiderDebug.log("++++++++++++量子-playerContent" + Json.toJson(realUrl));
        if(!realUrl.contains("m3u8")){
            String videoContent = OkHttp.string(realUrl, getHeader());
            Matcher mainMatcher = Pattern.compile("var main = \"(.*?)\";").matcher(videoContent);
            String mainUrl = mainMatcher.find() ? mainMatcher.group(1) : "";
            realUrl= URLUtil.getHost(new URL(realUrl)).toString()+mainUrl;
        }
        return Result.get().url(realUrl).header(getHeader()).string();
    }

}
