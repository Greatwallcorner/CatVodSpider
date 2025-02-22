package com.github.catvod.spider;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.URLEncodeUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Utils;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mtyy extends Spider {

    private static final String siteUrl = "https://mtyy2.com";




    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl));
        List<String> typeNames = Arrays.asList("连续剧", "电影", "综艺", "动漫", "短剧", "纪录片");
        List<String> typeIds = Arrays.asList("1", "2", "3", "4", "5", "6");
        for (int i = 0; i < typeIds.size(); i++) {
            classes.add(new Class(typeIds.get(i), typeNames.get(i)));
            //  filters.put(typeIds.get(i), Arrays.asList(new Filter("filters", "過濾", Arrays.asList(new Filter.Value("全部", ""), new Filter.Value("單人作品", "individual"), new Filter.Value("中文字幕", "chinese-subtitle")))));

        }
        getVodList(doc, list);
        SpiderDebug.log("++++++++++++麦田-homeContent" + Json.toJson(list));
        return Result.string(classes, list);
    }

    private void getVodList(Document doc, List<Vod> list) {
        for (Element div : doc.select(".public-list-box")) {
            String id = siteUrl + div.select(".public-list-div > a").attr("href");
            String name = div.select(".public-list-div > a").attr("title");
            String pic = div.select("img").attr("data-src");
            if (pic.isEmpty()) pic = div.select("img").attr("src");
            String remark = div.select("span.public-list-prb").text();

            list.add(new Vod(id, name, pic, remark));
        }
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        String target = siteUrl + "/vodtype/" + tid + "-" + pg + ".html";
        //String filters = extend.get("filters");
        String html = OkHttp.string(target);
        Document doc = Jsoup.parse(html);
        getVodList(doc, list);
        String total = "" + Integer.MAX_VALUE;


        SpiderDebug.log("++++++++++++麦田-categoryContent" + Json.toJson(list));
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(total) / 42 + ((Integer.parseInt(total) % 42) > 0 ? 1 : 0), 42, Integer.parseInt(total)).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {

        SpiderDebug.log("++++++++++++麦田-detailContent--args" + Json.toJson(ids));
        Document doc = Jsoup.parse(OkHttp.string(ids.get(0), getHeader()));

        Elements sources = doc.select("div.swiper-wrapper > a.swiper-slide");
        Elements urlSources = doc.select("div.anthology-list > div.anthology-list-box");

        StringBuilder vod_play_url = new StringBuilder();
        StringBuilder vod_play_from = new StringBuilder();
        for (int i = 0; i < sources.size(); i++) {


            String playFromText = sources.get(i).text();
            vod_play_from.append(playFromText).append("$$$");
            Element urlSource = urlSources.get(0);
            Elements urls = urlSource.select("ul.anthology-list-play  > li > a");
            for (int j = 0; j < urls.size(); j++) {
                Element a = urls.get(j);
                String href = siteUrl + a.attr("href");
                String text = a.text();
                vod_play_url.append(text).append("$").append(href);
                boolean notLastEpisode = j < urls.size() - 1;
                vod_play_url.append(notLastEpisode ? "#" : "$$$");
            }
        }
        String title = doc.select(".this-desc-title").text();
        String classifyName = doc.select("body > div.ds-vod-detail.rel > div > div.slide-desc-box > div.this-desc-labels.flex > span.focus-item-label-original.this-tag.bj2").text();
        String year = doc.select("body > div.ds-vod-detail.rel > div > div.slide-desc-box > div.this-desc-labels.flex > span.this-tag.this-b").text();
        String area = doc.select("body > div.ds-vod-detail.rel > div > div.slide-desc-box > div.this-desc-info > span:nth-child(2)").text();
        String remark = doc.select("body > div.ds-vod-detail.rel > div > div.slide-desc-box > div.detail-score.wow.fadeInUp.animated > div.play-score.cf > div.fraction").text();
        String bg = doc.select(".this-pic-bj").attr("style");
        Matcher matcher = Pattern.compile("url\\('(.*?)'\\)").matcher(bg);
        String vodPic = matcher.find() ? matcher.group(1) : "";
        String director = doc.select("body > div.ds-vod-detail.rel > div > div.slide-desc-box > div:nth-child(6) > a").text();

        String actor = doc.select("body > div.ds-vod-detail.rel > div > div.slide-desc-box > div:nth-child(7) > a").text();

        String brief = doc.select("#height_limit").text();
        ;
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
        SpiderDebug.log("++++++++++++麦田-detailContent" + Json.toJson(vod));
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
      // String session = MtyyKT.init();
        String searchUrl = siteUrl + "/vodsearch/-------------.html?wd=" + URLEncodeUtil.encode(key);

        AtomicReference<String> html = new AtomicReference<String>("");//OkHttp.string(searchUrl,Utils.webHeaders(searchUrl, session))

       if (html.get().contains("验证码") || StringUtils.isAllBlank(html.get())) {
            MtyyKT.verify(searchUrl, html);
        } else if (html.get().contains("请不要频繁操作")) {
            Utils.notify("麦田影视提示：请不要频繁操作");
        }
     /*    String res = OkHttp.string(searchUrl, Utils.webHeaders(searchUrl, "PHPSESSID=uv4hpgb9oqmp5lk6igtorlls38"));
       html.set(res);*/
        String htm = OkHttp.string(searchUrl, Utils.webHeaders(searchUrl, html.get()));
        Document document = Jsoup.parse(htm);
        SpiderDebug.log("++++++++++++麦田-搜索结果" + html.get());

        List<Vod> list = new ArrayList<>();


        getVodList(document, list);

        SpiderDebug.log("++++++++++++麦田-searchContent" + Json.toJson(list));
        return Result.string(list);
    }


    private byte[] getVerifyCodePic(String url) {
        String codeUrl = this.siteUrl + "/verify/index.html?";
        Response resp = null;
        try {
            resp = OkHttp.newCall(codeUrl, Utils.webHeaders(url));
            return resp.body().bytes();
        } catch (IOException e) {
            SpiderDebug.log("请求验证码出错:" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            resp.close();
        }
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String content = OkHttp.string(id, getHeader());
        Matcher matcher = Pattern.compile("player_aaaa=(.*?)</script>").matcher(content);
        String json = matcher.find() ? matcher.group(1) : "";
        JSONObject player = new JSONObject(json);
        String url = player.getString("url");
        String urlNext = player.getString("url_next");
        SpiderDebug.log("++++++++++++麦田-playerContent" + Json.toJson(url));
//        if (player.getInt("encrypt") == 1) {
//            url = URLDecoder.decode(url);
//            urlNext = URLDecoder.decode(urlNext);
//        } else if (player.getInt("encrypt") == 2) {
//            url = URLDecoder.decode(Base64.decodeStr(url));
//            urlNext = URLDecoder.decode(Base64.decodeStr(urlNext));
//        }
//
//        String iframeUrl = this.siteUrl + "/static/player/ffzy.php?url=" + url + "&jump=" + urlNext + "&thumb=" + player.getString("vod_pic_thumb") + "&id=" + player.getString("id") + "&nid=" + player.getInt("nid");
//        String iframeContent = OkHttp.string(iframeUrl);
//        String encodeUrl = Utils.getVar(iframeContent, "urls");
//        String realUrl = getVideoInfo(encodeUrl);
        return Result.get().url(url).header(getHeader()).string();
    }

    String getVideoInfo(String _0x38afx2) {
        String string = _0x38afx2.substring(8);
        String substr = new String(Base64.decode(string));
        return cn.hutool.core.net.URLDecoder.decode(substr.substring(8, substr.length() - 8), StandardCharsets.UTF_8);
    }

}
