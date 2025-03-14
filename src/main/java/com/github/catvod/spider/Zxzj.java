package com.github.catvod.spider;/*
/**

@auther lushunming
*/


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.ProxyVideo;
import com.github.catvod.utils.Util;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Zxzj extends Spider {

    private final String siteUrl = "https://www.zxzjhd.com";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/100.0.4896.77 Mobile/15E148 Safari/604.1");
        header.put("Connection", "keep-alive");
        header.put("Referer", "https://www.zxzj.pro/");
        header.put("sec-fetch-dest", "iframe");
        header.put("sec-fetch-mode", "navigate");
        header.put("sec-fetch-site", "cross-site");
        return header;
    }

    private Map<String, String> getVideoHeader() {
        Map<String, String> header = new HashMap<>();

        header.put("Accept", "*/*");
        header.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,de;q=0.6");
        header.put("Cache-Control", "no-cache");
        header.put("Connection", "keep-alive");
        header.put("Pragma", "no-cache");

        header.put("Sec-Fetch-Dest", "video");
        header.put("Sec-Fetch-Mode", "no-cors");
        header.put("Sec-Fetch-Site", "cross-site");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
       /* header.put("sec-ch-ua", "\"Chromium\";v=\"124\", \"Google Chrome\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "\"Windows\"");*/
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
            String name = div.select(".stui-vodlist__detail >h4.title > a").text();
            String pic = div.select(".stui-vodlist__box > a.stui-vodlist__thumb").attr("data-original");
            if (pic.isEmpty()) pic = div.select("img").attr("src");
            String remark = div.select(".stui-vodlist__box > a.stui-vodlist__thumb > span").text();

            list.add(new Vod(id, name, pic, remark));
        }
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        String[] arr = tid.split("\\.");
        String target = siteUrl + arr[0] + "-" + pg + ".html";
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
        Document doc = Jsoup.parse(OkHttp.string(this.siteUrl + ids.get(0), getHeader()));

        Elements sources = Objects.requireNonNull(doc.select("ul.stui-content__playlist").first()).children();
        StringBuilder vod_play_url = new StringBuilder();
        StringBuilder vod_play_from = new StringBuilder("在线之家").append("$$$");

        for (int i = 0; i < sources.size(); i++) {
            String href = sources.get(i).select("a").attr("href");
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
        String searchUrl = siteUrl + "/vodsearch/-------------.html?wd=" + key + "&submit=";
        String html = OkHttp.string(searchUrl);
        if (html.contains("Just a moment")) {
            Util.notify("在线之家资源需要人机验证");
        }
        Document document = Jsoup.parse(html);
        List<Vod> list = new ArrayList<>();
        getVods(list, document);

        SpiderDebug.log("++++++++++++在线之家-searchContent" + Json.toJson(list));
        return Result.string(list);
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String content = OkHttp.string(this.siteUrl + id, getHeader());
        Matcher matcher = Pattern.compile("player_aaaa=(.*?)</script>").matcher(content);
        String json = matcher.find() ? matcher.group(1) : "";
        org.json.JSONObject player = new JSONObject(json);
        String realUrl = player.getString("url");
        String videoContent = OkHttp.string(realUrl, getHeader());
        Matcher matcher2 = Pattern.compile("result_v2 =(.*?);").matcher(videoContent);
        String json2 = matcher2.find() ? matcher2.group(1) : "";
        org.json.JSONObject jsonObject = new JSONObject(json2);
        String encodedStr = jsonObject.getString("data");
        realUrl = new String(new BigInteger(StrUtil.reverse(encodedStr), 16).toByteArray());
        SpiderDebug.log("++++++++++++在线之家-playerContent" + Json.toJson(realUrl));
        Map<String, String> header = getVideoHeader();
        String temp = decodeStr(realUrl);
        return Result.get().url(ProxyVideo.buildCommonProxyUrl(temp, header)).header(getVideoHeader()).string();
    }

    String decodeStr(String _0x267828) {
        int _0x5cd2b5 = (_0x267828.length() - 7) / 2;
        String _0x2191ed = _0x267828.substring(0, _0x5cd2b5);
        String _0x35a256 = _0x267828.substring(_0x5cd2b5 + 7);
        return _0x2191ed + _0x35a256;
    }

    String cryptJs(String text, String key, String iv) {
        byte[] key_value = key.getBytes(StandardCharsets.UTF_8);
        byte[] iv_value = iv.getBytes(StandardCharsets.UTF_8);


        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key_value, iv_value);

        String content = new String(aes.decrypt(text), StandardCharsets.UTF_8);

        return content;
    }

}
