package com.github.catvod.spider;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;

import com.github.catvod.utils.Utils;
import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhixc
 */
public class QiLeSo extends Cloud {

    private final String URL = "https://www.qileso.com/";

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", Utils.CHROME);
        return header;
    }

    @Override
    public String detailContent(List<String> shareUrl) throws Exception {
        SpiderDebug.log("qileso detail args:" + Json.toJson(shareUrl));

        String html = OkHttp.string(shareUrl.get(0), getHeader());
        Document doc = Jsoup.parse(html);
        Element elements = doc.selectFirst("#body > div > div.thread-body > div.thread-content.message.break-all > p > a");
        SpiderDebug.log("qileso detail shareurl:" + elements.attr("href"));


        String result = super.detailContent(ImmutableList.of(elements.attr("href")));
        SpiderDebug.log("qileso detail:" + result);
        return result;
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String url = URL + "?s=" + URLEncoder.encode(key, Charset.defaultCharset().name());

        String html = OkHttp.string(url, getHeader());
        Document doc = Jsoup.parse(html);
        List<Vod> list = new ArrayList<>();

        Elements elements = doc.select(" ul.list-group > li.list-group-item > div.subject > h2 > a");
        for (Element element : elements) {
            String id = element.attr("href");
            String name = element.text();
            list.add(new Vod(id, name, ""));
        }

        SpiderDebug.log("qileso searchContent:" + Result.string(list));

        return Result.string(list);
    }
}
