package com.github.catvod.spider;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Filter;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WuMingMusic extends Spider {

    private final String siteUrl = "http://www.mvmp3.com";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        header.put("Referer", "http://www.mvmp3.com/");
        return header;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {

        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        LinkedHashMap<String, List<Filter>> filters = new LinkedHashMap<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl));
        List<String> typeNames = Arrays.asList("TOP榜单", "DJ舞曲", "恋爱的歌", "粤语排行");
        List<String> typeIds = Arrays.asList("top", "djwuqu", "love", "yytop");
        for (int i = 0; i < typeIds.size(); i++) {
            classes.add(new Class(typeIds.get(i), typeNames.get(i)));
            //  filters.put(typeIds.get(i), Arrays.asList(new Filter("filters", "過濾", Arrays.asList(new Filter.Value("全部", ""), new Filter.Value("單人作品", "individual"), new Filter.Value("中文字幕", "chinese-subtitle")))));

        }
        for (Element div : doc.select("body > div.container > div.index > ul > li")) {
            String mv = div.select("div.song > div > div > a").attr("href");
            String mp3 = div.select("div.song > div > a").attr("href");
            String id = siteUrl + (StringUtil.isBlank(mv) ? mp3 : mv);

            String name = div.select("div.song > div  > a").attr("title");
            String pic = "";

            String remark = div.select("div.size").text();

            list.add(new Vod(id, name, pic, remark));
        }
        SpiderDebug.log("++++++++++++无名音乐-homeContent" + Json.toJson(list));
        return Result.string(classes, list);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        List<Vod> list = new ArrayList<>();
        String target = siteUrl + "/list/" + tid + ".html";
        //String filters = extend.get("filters");
        String html = OkHttp.string(target);
        Document doc = Jsoup.parse(html);
        for (Element div : doc.select("body > div.container > div:nth-child(3) > ul > li")) {
            String mv = div.select("div.lk > div.mv  > a").attr("href");
            String mp3 = div.select("div.lk >  a").attr("href");
            String id = siteUrl + (StringUtil.isBlank(mv) ? mp3 : mv);

            String name = div.select("div.lk >  a").attr("title");
            String pic = "";
            String remark = div.select("div.lk >  a").attr("title");

            list.add(new Vod(id, name, pic, remark));
        }
        String total = RegExUtils.removeAll(doc.select("body > div.container > div:nth-child(4) > div:nth-child(2) > a:nth-child(4)").text(), "[^0-9]+");
        total = StringUtils.isAllBlank(total) ? Integer.MAX_VALUE + "" : total;
        SpiderDebug.log("++++++++++++无名音乐-categoryContent" + Json.toJson(list));
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(total) / 100 + ((Integer.parseInt(total) % 100) > 0 ? 1 : 0), 100, Integer.parseInt(total)).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        if (ids.get(0).contains("mp4")) {
            SpiderDebug.log("++++++++++++无名音乐-detailContent--args" + Json.toJson(ids));
            Document doc = Jsoup.parse(OkHttp.string(ids.get(0), getHeader()));
            Elements circuits = doc.select(".module-tab-item.tab-item");
            Elements sources = doc.select("[class=scroll-content]");
            String vod_play_from = "播放" + "$$$";
            String vod_play_url = "播放" + "$" + ids.get(0) + "$$$";

            String title = doc.select("body > div.container > div:nth-child(1) > div:nth-child(1) > h1").text();
            String classifyName = doc.select("body > div.container > div:nth-child(1) > div:nth-child(3) > p:nth-child(1) > b:nth-child(2)").text();
            String vodPic = doc.select("body > div.container > div:nth-child(1) > div:nth-child(2) > p:nth-child(1) > a > img").attr("src");
            String actor = doc.select("body > div.container > div:nth-child(1) > div:nth-child(2) > h2:nth-child(3) > a").text();
            String brief = "";
            Vod vod = new Vod();
            vod.setVodId(ids.get(0));
            vod.setVodYear("");
            vod.setVodName(title);
            vod.setVodArea("");
            vod.setVodActor(actor);
            vod.setVodPic(vodPic);
            vod.setVodRemarks("");
            vod.setVodContent(brief);
            vod.setVodDirector("");
            vod.setTypeName(classifyName);
            vod.setVodPlayFrom(vod_play_from);
            vod.setVodPlayUrl(vod_play_url);
            SpiderDebug.log("++++++++++++无名音乐-detailContent" + Json.toJson(vod));
            return Result.string(vod);
        } else {
            SpiderDebug.log("++++++++++++无名音乐-detailContent--args" + Json.toJson(ids));
            Document doc = Jsoup.parse(OkHttp.string(ids.get(0), getHeader()));
            Elements circuits = doc.select(".module-tab-item.tab-item");
            Elements sources = doc.select("[class=scroll-content]");
            String vod_play_from = "播放" + "$$$";
            String vod_play_url = "播放" + "$" + ids.get(0) + "$$$";

            String title = doc.select(".playhimg > h1").text();
            String classifyName = doc.select("body > div.container > div:nth-child(1) > div:nth-child(3) > p:nth-child(1) > b:nth-child(2)").text();
            String vodPic = doc.select("body > div.container > div:nth-child(1) > div:nth-child(1) > div.playhimg > img").attr("src");
            String actor = doc.select(".playhimg > h1").text();
            String brief = "";
            Vod vod = new Vod();
            vod.setVodId(ids.get(0));
            vod.setVodYear("");
            vod.setVodName(title);
            vod.setVodArea("");
            vod.setVodActor(actor);
            vod.setVodPic(vodPic);
            vod.setVodRemarks("");
            vod.setVodContent(brief);
            vod.setVodDirector("");
            vod.setTypeName(classifyName);
            vod.setVodPlayFrom(vod_play_from);
            vod.setVodPlayUrl(vod_play_url);
            SpiderDebug.log("++++++++++++无名音乐-detailContent" + Json.toJson(vod));
            return Result.string(vod);
        }

    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String searchUrl = siteUrl + "/so.php?wd=";
        String html = OkHttp.string(searchUrl + key);
        Document document = Jsoup.parse(html);
        List<Vod> list = new ArrayList<>();


        for (Element div : document.select("body > div.container > div > div.search > ul > li")) {
            String mv = div.select("div.lk > div.mv > a").attr("href");
            String mp3 = div.select("div.lk > a").attr("href");
            String vodId = siteUrl + (StringUtil.isBlank(mv) ? mp3 : mv);
            String name = div.select("div.lk > a").attr("title");
            String remark = div.select("div.lk > a").text();
            String pic = "";
            list.add(new Vod(vodId, name, pic, remark));
        }

        SpiderDebug.log("++++++++++++无名音乐-searchContent" + Json.toJson(list));
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        if (id.contains("mp4")) {
            String content = OkHttp.string(id, getHeader());
            Matcher matcher = Pattern.compile("url:'(.*?)'}").matcher(content);
            String json = matcher.find() ? matcher.group(1) : "";
            return Result.get().url(json).header(getHeader()).string();
        } else {
            String content = OkHttp.string(id, getHeader());
            Matcher matcher = Pattern.compile("url:\"(.*?)\",pic").matcher(content);
            String json = matcher.find() ? matcher.group(1) : "";
            String realUrl = OkHttp.getLocation(json, getHeader());
            SpiderDebug.log("++++++++++++无名音乐-playerContent" + Json.toJson(realUrl));
            return Result.get().url(realUrl).header(getHeader()).string();
        }
    }

}
