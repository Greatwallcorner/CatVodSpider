package com.github.catvod.spider.unavailable;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QxiTv extends Spider {

    private static final String siteUrl = "https://www.7xi.tv";
    private static final String cateUrl = siteUrl + "/vodtype/";
    private static final String searchUrl = siteUrl + "/vodsearch/page/1/wd/";
    private static final String playUrl = siteUrl + "/FosiPlayer/API.php";
    private static final List<Class> classList = Class.parseFromFormatStr("电影=1&电视剧=2&综艺=3&动漫=4");

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Util.CHROME);
        return headers;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Vod> list = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeaders()));
        for (Element element : doc.select("a.public-list-exp")) {
            try {
                String pic = element.select("img").attr("data-src");
                String url = element.attr("href");
                String name = element.attr("title");
                if (!pic.startsWith("http")) {
                    pic = siteUrl + pic;
                }
                list.add(new Vod(url, name, pic));
            } catch (Exception e) {

            }

        }
        return Result.string(classList, list);
    }

    public String MD5(String string) {
        // 创建 MD5 实例
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算 MD5 哈希值
            byte[] hashBytes = md.digest(string.getBytes());

            // 将字节数组转换为十六进制字符串表示
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // 输出加密后的 MD5 字符串
            System.out.println("MD5 加密: " + hexString.toString());
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String url = cateUrl + tid;
        if(!Objects.equals(pg, "1")){
            url += "-" + pg;
        }
        String res = OkHttp.string(url, getHeaders());
        Document document = Jsoup.parse(res);
        List<Vod> list = new ArrayList<>();
        Elements vodList = document.select("a.public-list-exp");
        for (Element element : vodList) {
            String vodId = element.attr("href");
            String title = element.attr("title");
            String img = element.select("img").attr("data-src");
            String remark = element.select(".public-list-prb").val();
            list.add(new Vod(vodId, title, img, remark));
        }
        return Result.string(classList, list);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Document doc = Jsoup.parse(OkHttp.string(siteUrl + ids.get(0), getHeaders()));
        String name = doc.select("div.this-desc-title").text();
        String pic = doc.select("div.this-pic-bj").attr("style").replace("background-image: url('", "").replace("')", "");
        String year = doc.select("div.this-desc-info > span").get(1).text();

        // 播放源
        Elements tabs = doc.select("a.swiper-slide");
        Elements list = doc.select("div.anthology-list-box.none");
        String PlayFrom = "";
        String PlayUrl = "";
        for (int i = 0; i < tabs.size(); i++) {
            String tabName = tabs.get(i).text();
            if (!"".equals(PlayFrom)) {
                PlayFrom = PlayFrom + "$$$" + tabName;
            } else {
                PlayFrom = PlayFrom + tabName;
            }
            Elements li = list.get(i).select("a");
            String liUrl = "";
            for (int i1 = 0; i1 < li.size(); i1++) {
                if (!"".equals(liUrl)){
                    liUrl = liUrl + "#" +  li.get(i1).text() + "$" + li.get(i1).attr("href");
                }else {
                    liUrl = liUrl + li.get(i1).text() + "$" + li.get(i1).attr("href");
                }
            }
            if (!"".equals(PlayUrl)) {
                PlayUrl = PlayUrl + "$$$" + liUrl;
            }else {
                PlayUrl = PlayUrl + liUrl;
            }
        }

        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(pic);
        vod.setVodYear(year);
        vod.setVodName(name);
        vod.setVodPlayFrom(PlayFrom);
        vod.setVodPlayUrl(PlayUrl);
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(searchUrl.concat(URLEncoder.encode(key)).concat(".html"), getHeaders()));
        for (Element element : doc.select("a.public-list-exp")) {
            try {
                String pic = element.select("img").attr("data-src");
                String url = element.attr("href");
                String name = element.attr("title");
                if (!pic.startsWith("http")) {
                    pic = siteUrl + pic;
                }
                String id = url.split("/")[2];
                list.add(new Vod(id, name, pic));
            } catch (Exception e) {
            }
        }
        return Result.string(list);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        Document doc = Jsoup.parse(OkHttp.string(siteUrl.concat(id),getHeaders()));
        String regex = "\"url\":\"(.*?)m3u8\",";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(doc.html());
        String url = "";
        if (matcher.find()) {
            url = matcher.group(1);
            url = url.replace("\\/","/") + "m3u8";
        }

        return Result.get().url(url).header(getHeaders()).string();
    }
}