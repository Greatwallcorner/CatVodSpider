package com.github.catvod.spider;/*
 * @File     : changzhang.js
 * @Author   : jade
 * @Date     : 2024/2/2 16:02
 * @Email    : jadehh@1ive.com
 * @Software : Samples
 * @Desc     :
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

public class ChangZhang extends Spider {

    private final String siteUrl = "https://www.czys.pro";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", "myannoun=1; Hm_lvt_0653ba1ead8a9aabff96252e70492497=2718862211; Hm_lvt_06341c948291d8e90aac72f9d64905b3=2718862211; Hm_lvt_07305e6f6305a01dd93218c7fe6bc9c3=2718862211; Hm_lpvt_07305e6f6305a01dd93218c7fe6bc9c3=2718867254; Hm_lpvt_06341c948291d8e90aac72f9d64905b3=2718867254; Hm_lpvt_0653ba1ead8a9aabff96252e70492497=2718867254");
        header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/100.0.4896.77 Mobile/15E148 Safari/604.1");
        header.put("Connection", "keep-alive");
        header.put("Host", "www.czys.pro");
        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl));

        for (Element div : doc.select(".navlist > li ")) {
            classes.add(new Class(div.select(" a").attr("href"), div.select(" a").text()));
        }

        getVods(list, doc);
        SpiderDebug.log("++++++++++++厂长-homeContent" + Json.toJson(list));
        return Result.string(classes, list);
    }

    private void getVods(List<Vod> list, Document doc) {
        for (Element div : doc.select(".bt_img.mi_ne_kd > ul >li")) {
            String id = div.select(".dytit > a").attr("href");
            String name = div.select(".dytit > a").text();
            String pic = div.select("img").attr("data-original");
            if (pic.isEmpty()) pic = div.select("img").attr("src");
            String remark = div.select(".hdinfo > span").text();

            list.add(new Vod(id, name, pic, remark));
        }
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        String target = siteUrl + tid + "/page/" + pg;
        //String filters = extend.get("filters");
        String html = OkHttp.string(target);
        Document doc = Jsoup.parse(html);
        getVods(list,doc);
        String total = "" + Integer.MAX_VALUE;


        SpiderDebug.log("++++++++++++厂长-categoryContent" + Json.toJson(list));
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(total) / 25 + ((Integer.parseInt(total) % 25) > 0 ? 1 : 0), 25, Integer.parseInt(total)).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {

        SpiderDebug.log("++++++++++++厂长-detailContent--args" + Json.toJson(ids));
        Document doc = Jsoup.parse(OkHttp.string(ids.get(0), getHeader()));

        Elements sources = doc.select("div.paly_list_btn > a");
        StringBuilder vod_play_url = new StringBuilder();
        StringBuilder vod_play_from = new StringBuilder("厂长").append("$$$");

        for (int i = 0; i < sources.size(); i++) {
            String href = sources.get(i).attr("href");
            String text = sources.get(i).text();
            vod_play_url.append(text).append("$").append(href);
            boolean notLastEpisode = i < sources.size() - 1;
            vod_play_url.append(notLastEpisode ? "#" : "$$$");
        }

        String title = doc.select(" div.dytext.fl > div > h1").text();
        String classifyName = doc.select(".moviedteail_list > li:nth-child(1)  > a").text();
        String year = doc.select(".moviedteail_list > li:nth-child(3)  > a").text();
        String area = doc.select(".moviedteail_list > li:nth-child(2)  > a").text();
        String remark = doc.select(".yp_context").text();
        String vodPic = doc.select(" div.dyxingq > div > div.dyimg.fl > img").attr("src");

        String director = doc.select(".moviedteail_list > li:nth-child(6)  > a").text();

        String actor = doc.select(".moviedteail_list > li:nth-child(8)  > a").text();

        String brief = doc.select(".yp_context").text();
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
        SpiderDebug.log("++++++++++++厂长-detailContent" + Json.toJson(vod));
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String searchUrl = siteUrl + "/daoyongjiekoshibushiyoubing?q=";
        String html = OkHttp.string(searchUrl + key);
        if (html.contains("Just a moment")) {
            Utils.notify("厂长资源需要人机验证");
        }
        Document document = Jsoup.parse(html);
        List<Vod> list = new ArrayList<>();
        getVods(list, document);

        SpiderDebug.log("++++++++++++厂长-searchContent" + Json.toJson(list));
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
        SpiderDebug.log("++++++++++++厂长-playerContent" + Json.toJson(realUrl));

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
