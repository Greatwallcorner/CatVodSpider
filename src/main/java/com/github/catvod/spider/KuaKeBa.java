package com.github.catvod.spider;

import cn.hutool.core.util.URLUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
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

/**
 * 夸克吧
 *
 * @author lushunming
 */
public class KuaKeBa extends Cloud {

    private final String siteUrl = "https://www.kuakeba.top/yunpan";


    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
        return header;
    }

    private Map<String, String> getHeaderWithCookie() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Util.CHROME);
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
        Elements elements = doc.select(" div.catleader > ul > li > a");
        for (Element e : elements) {
            String url = e.attr("href");
            String name = e.text();
            classes.add(new Class(url, name));
        }

        return Result.string(classes, parseVodListFromDoc(doc));
    }

    private List<Vod> parseVodListFromDoc(Document doc) {
        List<Vod> list = new ArrayList<>();
        Elements elements = doc.select(" article.excerpt");
        for (Element e : elements) {
            String vodId = e.selectFirst(" a").attr("href");
            String vodPic = e.selectFirst(" img").attr("data-src");
            if (!vodPic.startsWith("http")) {
                vodPic = URLUtil.completeUrl(siteUrl, vodPic);
            }
            String vodName = e.selectFirst(" header > h2 > a").text();
            String vodRemarks = "";
            list.add(new Vod(vodId, vodName, vodPic, vodRemarks));
        }
        return list;
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {

        Document doc = Jsoup.parse(OkHttp.string(String.format("%s/page/%s", tid, pg), getHeader()));
        List<Vod> list = parseVodListFromDoc(doc);
        int total = (Integer.parseInt(pg) + 1) * 20;
        return Result.get().vod(list).page(Integer.parseInt(pg), Integer.parseInt(pg) + 1, 20, total).string();
    }


    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        Document doc = Jsoup.parse(OkHttp.string(vodId, getHeader()));

        Vod item = new Vod();
        item.setVodId(vodId);
        item.setVodName(doc.selectFirst(" h1.article-title > a").text());
        item.setVodPic(doc.selectFirst("  img").attr("src"));
        item.setVodArea(getStrByRegex(Pattern.compile("导演:(.*?)<br"), doc.html()));
        item.setVodDirector(getStrByRegex(Pattern.compile("地区:(.*?)<br"), doc.html()));
        item.setVodActor(getStrByRegex(Pattern.compile("主演:(.*?)<br"), doc.html()));
        item.setVodYear(getStrByRegex(Pattern.compile("上映日期:(.*?)<br"), doc.html()));
        item.setVodRemarks("");
        item.setVodContent(getStrByRegex(Pattern.compile("<h2>剧情简介</h2>\n(.*?)</p>"), doc.html()).replace("<p>", ""));

        List<String> shareLinks = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (Element element : doc.select("article.article-content p a")) {
            if (element.attr("href").matches(Quark.patternQuark)) {
                shareLinks.add(element.attr("href"));
            } else if (element.attr("href").contains("tag")) {
                tags.add(element.attr("href"));
            }
        }

        item.setTypeName(String.join(",", tags));
        shareLinks.replaceAll(String::trim);
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
        String searchURL = siteUrl + String.format("/?s=%s", URLEncoder.encode(key));
        String html = OkHttp.string(searchURL, getHeaderWithCookie());
        Document doc = Jsoup.parse(html);

        return Result.string(parseVodListFromDoc(doc));
    }
}