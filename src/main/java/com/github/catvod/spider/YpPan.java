package com.github.catvod.spider;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class YpPan extends Ali {
    private final String host = Util.base64Decode("aHR0cHM6Ly93d3cueXBwYW4uY29t");

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        return searchContent(key, quick, "1");
    }

    @NotNull
    private static String getRemarks(Element i) {
        Elements select = i.select(".post-meta span");
        return select.get(0).text() + " " + select.get(2).text();
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) throws Exception {
        String url = host;
        if (!Objects.equals(pg, "1")) {
            url = url + "/page/" + pg;
        }
        url += "/?s=" + URLEncoder.encode(key, Charset.defaultCharset().name());
        String doc = OkHttp.string(url, Util.webHeaders(url));
        Document document = Jsoup.parse(doc);
        Elements items = document.body().select("#page #main #content .post");
        ArrayList<Vod> vodList = Lists.newArrayList();
        items.forEach(i -> {
                    Elements one = i.select(".post-title a");
                    Vod vod = new Vod(one.attr("href"), one.text(), defaultPicUrl, getRemarks
                            (i));
                    vodList.add(vod);
                }
        );
        return Result.string(vodList);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String doc = OkHttp.string(ids.get(0), Util.webHeaders(host));
        Elements document = Jsoup.parse(doc).body().select("#page #main #content");
        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodName(document.select(".post-title").text());
        vod.setVodPic(defaultPicUrl);
        Elements entry = document.select(".entry p");
        String vodContent = "";
        if(!entry.isEmpty()) vodContent += entry.get(1).text().replace("资源简介：", "");
        if(entry.size() >= 4) {
            vodContent += entry.get(1).text();
            vod.setVodTag(entry.get(3).text().replace("关键词：", "").replace("#", ""));
        }
        vod.setVodContent(vodContent);
        System.out.println(entry.get(2).text());
        Matcher matcher = pattern2.matcher(entry.get(2).text());
        boolean b = matcher.find();
        List<String> list = Lists.newArrayList(matcher.group());
        vod.setVodPlayFrom(detailContentVodPlayFrom(list));
        vod.setVodPlayUrl(detailContentVodPlayUrl(list));
        return Result.string(Lists.newArrayList(vod));
    }
}
