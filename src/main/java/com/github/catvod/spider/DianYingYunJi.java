package com.github.catvod.spider;


import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.catvod.spider.Quark.patternQuark;

/**
 * 电影云集
 *
 * @author lushunming
 * @createdate 2024-12-03
 */
public class DianYingYunJi extends Cloud {

    private final String siteUrl = "https://dyyjpro.com";


    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Utils.CHROME);
        return header;
    }

    private Map<String, String> getHeaderWithCookie() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Utils.CHROME);
        header.put("cookie", "esc_search_captcha=1; result=43");
        return header;
    }

    @Override
    public void init(String extend) throws Exception {

        super.init(extend);
    }

    @Override
    public String homeContent(boolean filter) {
        List<Class> classes = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeader()));
        Elements elements = doc.select(" #header-navbar > li.menu-item > a");
        for (Element e : elements) {
            String url = e.attr("href");
            String name = e.text();
            if (url.contains(siteUrl)) {
                classes.add(new Class(url, name));
            }

        }

        return Result.string(classes, parseVodListFromDoc(doc));
    }

    private List<Vod> parseVodListFromDoc(Document doc) {
        List<Vod> list = new ArrayList<>();
        Elements elements = doc.select(" article.post-item");
        for (Element e : elements) {
            String vodId = e.selectFirst("h2.entry-title > a").attr("href");
            String vodPic = e.selectFirst(" div.entry-media > a").attr("data-bg");
            if (!vodPic.startsWith("http")) {
                vodPic = siteUrl + vodPic;
            }
            String vodName = e.selectFirst("h2.entry-title > a").text();
            String vodRemarks = "";
            list.add(new Vod(vodId, vodName, vodPic, vodRemarks));
        }
        return list;
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {

        Document doc = Jsoup.parse(OkHttp.string(String.format("%s/page/%s", tid, pg), getHeader()));
        List<Vod> list = parseVodListFromDoc(doc);
        int total = (Integer.parseInt(pg) + 1) * 19;
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(pg) + 1, 19, total).string();
    }


    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        Document doc = Jsoup.parse(OkHttp.string(vodId, getHeader()));

        Vod item = new Vod();
        item.setVodId(vodId);
        item.setVodName(doc.selectFirst(" h1.post-title").text());
        item.setVodPic(doc.selectFirst("article.post-content  img").attr("src"));
        String html = doc.select("article.post-content > p").text();
        item.setVodDirector(getStrByRegex(Pattern.compile("导演:(.*?)编剧:"), html));
        item.setVodArea(getStrByRegex(Pattern.compile("地区:(.*?)语言:"), html));
        item.setVodActor(getStrByRegex(Pattern.compile("主演:(.*?)类型:"), html));
        item.setVodYear(getStrByRegex(Pattern.compile("上映日期:(.*?)片长:"), html));
        item.setVodRemarks("");
        item.setVodContent(getStrByRegex(Pattern.compile("剧情简介(.*?)获奖情况"), html));

        List<String> shareLinks = new ArrayList<>();

        for (Element element : doc.select("article.post-content p a")) {
            if (element.attr("href").matches(patternQuark)) {
                shareLinks.add(element.attr("href").trim());
            }
        }

        item.setTypeName(doc.selectFirst(" span.meta-cat-dot").text());

        item.setVodPlayUrl(super.detailContentVodPlayUrl(shareLinks));
        item.setVodPlayFrom(super.detailContentVodPlayFrom(shareLinks));

        return Result.string(item);
    }

    private String getStrByRegex(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) return matcher.group(1).trim();
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, "1");
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        return searchContent(key, pg);
    }

    private String searchContent(String key, String pg) {
        String searchURL = siteUrl + String.format("?cat=&s=%s", URLEncoder.encode(key));
        String html = OkHttp.string(searchURL, getHeaderWithCookie());
        Document doc = Jsoup.parse(html);

        return Result.string(parseVodListFromDoc(doc));
    }
}