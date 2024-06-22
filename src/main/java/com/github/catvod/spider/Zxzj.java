package com.github.catvod.spider;/*
/**

@auther lushunming
*/


import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Zxzj extends Spider {

    private final String siteUrl = "https://www.zxzj.pro";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/100.0.4896.77 Mobile/15E148 Safari/604.1");
        header.put("Connection", "keep-alive");
        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl));

        for (Element div : doc.select(".stui-header__menu > li ")) {
            classes.add(new Class(div.select(" a").attr("href"), div.select(" a").text()));
        }

        getVods(list, doc);
        SpiderDebug.log("++++++++++++在线之家-homeContent" + Json.toJson(list));
        return Result.string(classes, list);
    }

    private void getVods(List<Vod> list, Document doc) {
        for (Element div : doc.select(".stui-vodlist >li")) {
            String id = div.select(".stui-vodlist__box > a.stui-vodlist__thumb").attr("href");
            String name = div.select(".stui-vodlist__detail > a").text();
            String pic = div.select(".stui-vodlist__box > a.stui-vodlist__thumb").attr("data-original");
            if (pic.isEmpty()) pic = div.select("img").attr("src");
            String remark = div.select(".stui-vodlist__box > a.stui-vodlist__thumb > span").text();

            list.add(new Vod(id, name, pic, remark));
        }
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        String target = siteUrl +"/list/"+ tid + "-" + pg+".html";
        //String filters = extend.get("filters");
        String html = OkHttp.string(target);
        Document doc = Jsoup.parse(html);
        getVods(list, doc);
        String total = "" + Integer.MAX_VALUE;


        SpiderDebug.log("++++++++++++在线之家-categoryContent" + Json.toJson(list));
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(total) / 12 + ((Integer.parseInt(total) % 12) > 0 ? 1 : 0), 12, Integer.parseInt(total)).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {

        SpiderDebug.log("++++++++++++在线之家-detailContent--args" + Json.toJson(ids));
        Document doc = Jsoup.parse(OkHttp.string(this.siteUrl+ids.get(0), getHeader()));

        Elements sources = Objects.requireNonNull(doc.select("ul.stui-content__playlist").first()).children();
        StringBuilder vod_play_url = new StringBuilder();
        StringBuilder vod_play_from = new StringBuilder("在线之家").append("$$$");

        for (int i = 0; i < sources.size(); i++) {
            String href = sources.get(i).attr("href");
            String text = sources.get(i).text();
            vod_play_url.append(text).append("$").append(href);
            boolean notLastEpisode = i < sources.size() - 1;
            vod_play_url.append(notLastEpisode ? "#" : "$$$");
        }

        String title = doc.select(" .stui-content__detail > h1").text();
        String classifyName = doc.select(".stui-content__detail > p:nth-child(1)").text();
        String year = doc.select(".stui-content__detail > p:nth-child(1)").text();
        String area = doc.select(".stui-content__detail > p:nth-child(1)").text();
        String remark = doc.select(".stui-content__detail > p:nth-child(1)").text();
        String vodPic = doc.select(" div.stui-content__thumb > a > img").attr("data-original");

        String director = doc.select(".stui-content__detail > p:nth-child(3)").text();

        String actor = doc.select(".stui-content__detail > p:nth-child(2)").text();

        String brief = doc.select(".detail-sketch").text();
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
        vod.setTypeName(classifyName);
        vod.setVodPlayFrom(vod_play_from.toString());
        vod.setVodPlayUrl(vod_play_url.toString());
        SpiderDebug.log("++++++++++++在线之家-detailContent" + Json.toJson(vod));
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String searchUrl = siteUrl + "/daoyongjiekoshibushiyoubing?q=";
        String html = OkHttp.string(searchUrl + key);
        if (html.contains("Just a moment")) {
            Utils.notify("在线之家资源需要人机验证");
        }
        Document document = Jsoup.parse(html);
        List<Vod> list = new ArrayList<>();
        getVods(list, document);

        SpiderDebug.log("++++++++++++在线之家-searchContent" + Json.toJson(list));
        return Result.string(list);
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String content = OkHttp.string(id, getHeader());
        Document document = Jsoup.parse(content);
        Elements iframe = document.select("iframe");
        String videoContent = OkHttp.string(iframe.get(0).attr("src"));
        document = Jsoup.parse(videoContent);
        Elements script = document.select("script");
        String rand = "";
        String player = "";
        for (Element element : script) {
            if (StringUtils.isNoneBlank(element.data())) {
                rand = Utils.getVar(element.data(), "rand");
                player = Utils.getVar(element.data(), "player");
            }
        }

        String videoInfo = cryptJs(player, "VFBTzdujpR9FWBhe", rand);
        JSONObject jsonElement = JSONUtil.parseObj(videoInfo);
        String realUrl = jsonElement.getStr("url");
        SpiderDebug.log("++++++++++++在线之家-playerContent" + Json.toJson(realUrl));

        return Result.get().url(realUrl).header(getHeader()).string();
    }


    String cryptJs(String text, String key, String iv) {
        byte[] key_value = key.getBytes(StandardCharsets.UTF_8);
        byte[] iv_value = iv.getBytes(StandardCharsets.UTF_8);


        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key_value, iv_value);

        String content = new String(aes.decrypt(text), StandardCharsets.UTF_8);

        return content;
    }

}
