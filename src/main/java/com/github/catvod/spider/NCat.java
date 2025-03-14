package com.github.catvod.spider;

import cn.hutool.core.text.UnicodeUtil;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.AESEncryption;
import com.github.catvod.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NCat extends Spider {

    private static final String siteUrl = "https://www.ncat3.app";

    //    private static final String siteUrl = "https://www.ncat3.com:51111";
    private static final String picUrl = "https://vres.wbadl.cn";
    private static final String cateUrl = siteUrl + "/show/";
    private static final String detailUrl = siteUrl + "/detail/";
    private static final String searchUrl = siteUrl + "/search?os=pc&k=";
    private static final String playUrl = siteUrl + "/play/";

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> map = Util.webHeaders(siteUrl);
        return map;
    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        String[] typeIdList = {"1", "2", "3", "4", "6"};
        String[] typeNameList = {"电影", "连续剧", "动漫", "综艺", "短剧"};
        for (int i = 0; i < typeNameList.length; i++) {
            classes.add(new Class(typeIdList[i], typeNameList[i]));
        }
        List<Vod> list = getVodList(siteUrl);
        return Result.string(classes, list);
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        String target = cateUrl + tid + "-----3-" + pg + ".html";
        List<Vod> list = getVodList(target);
        return Result.string(list);
    }

    private @NotNull List<Vod> getVodList(String link) {
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(link, getHeaders()));
        for (Element element : doc.select("div.module-item > a.v-item")) {
            try {
                String pic = picUrl + element.select("img:not([id])").attr("data-original");
                String url = element.select("a").attr("href");
                String name = element.select("div[class=v-item-title]:not([style])").text();
                if (!pic.startsWith("http")) {
                    pic = picUrl + pic;
                }
                String id = url.split("/")[2];
                list.add(new Vod(id, name, pic));
            } catch (Exception e) {

            }
        }
        return list;
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        Document doc = Jsoup.parse(OkHttp.string(detailUrl.concat(ids.get(0)), getHeaders()));
        String name = doc.select("div.detail-title strong:nth-child(2)").text();
        String pic = doc.select(".detail-pic img").attr("data-original");
        String year = doc.select("a.detail-tags-item").get(0).text();
        String desc = doc.select("div.detail-desc p").text();

        // 播放源
        Elements tabs = doc.select("a.source-item span");
        Elements list = doc.select("div.episode-list");
        String PlayFrom = "";
        String PlayUrl = "";
        for (int i = 0; i < tabs.size(); i++) {
            String tabName = tabs.get(i).text();
            if (Arrays.asList("超清", "4K(高峰不卡)").contains(tabName)) continue;
            if (!"".equals(PlayFrom)) {
                PlayFrom = PlayFrom + "$$$" + tabName;
            } else {
                PlayFrom = PlayFrom + tabName;
            }
            Elements li = list.get(i).select("a");
            String liUrl = "";
            for (int i1 = 0; i1 < li.size(); i1++) {
                if (!"".equals(liUrl)) {
                    liUrl = liUrl + "#" + li.get(i1).text() + "$" + li.get(i1).attr("href").replace("/play/", "");
                } else {
                    liUrl = liUrl + li.get(i1).text() + "$" + li.get(i1).attr("href").replace("/play/", "");
                }
            }
            if (!"".equals(PlayUrl)) {
                PlayUrl = PlayUrl + "$$$" + liUrl;
            } else {
                PlayUrl = PlayUrl + liUrl;
            }
        }

        Vod vod = new Vod();
        vod.setVodId(ids.get(0));
        vod.setVodPic(picUrl + pic);
        vod.setVodYear(year);
        vod.setVodName(name);
        vod.setVodContent(desc);
        vod.setVodPlayFrom(PlayFrom);
        vod.setVodPlayUrl(PlayUrl);
        return Result.string(vod);
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        List<Vod> list = new ArrayList<>();
        Document doc = Jsoup.parse(OkHttp.string(searchUrl.concat(URLEncoder.encode(key)).concat(".html"), getHeaders()));
        for (Element element : doc.select("a.search-result-item")) {
            try {
                String pic = picUrl + element.select("img:not([id])").attr("data-original");
                String url = element.attr("href");
                String name = element.select("img").attr("title");
                if (!pic.startsWith("http")) {
                    pic = picUrl + pic;
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
        Document doc = Jsoup.parse(OkHttp.string(playUrl.concat(id), getHeaders()));
        String regex = "window.whatTMDwhatTMDPPPP = '(.*?)'";
        String playSource = "playSource=\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(doc.html());
        String url = "";
        Pattern playSourcePattern = Pattern.compile(playSource);
        Matcher playSourceMatcher = playSourcePattern.matcher(doc.html());
        boolean b = playSourceMatcher.find();
        if (!b) {
            playSource = "playSource\\s*=\\s*(\\{[^{}]*\\});";
            Pattern compile = Pattern.compile(playSource);
            Matcher matcher1 = compile.matcher(doc.html());
            boolean b1 = matcher1.find();
            if (!b1) {
                SpiderDebug.log("获取播放链接 方式2 失败");
            } else {
                String group = matcher1.group(1).replaceAll("\\s", "");
                Pattern compile1 = Pattern.compile("src:\"([^\"]+)\"");
                Matcher matcher2 = compile1.matcher(group);
                boolean b2 = matcher2.find();
                if(b2){
                    url = matcher2.group(1);
                }
            }
        } else {
            if (matcher.find()) {
                url = matcher.group(1);
                String js = playSourceMatcher.group(1);
                String regex1 = "KKYS\\['safePlay'\\]\\(\\)\\['url'\\]\\(\"([^\"]+)\"\\)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(UnicodeUtil.toString(js));
                String iv = "VNF9aVQF!G*0ux@2hAigUeH3";
                if (matcher1.find()) {
                    iv = matcher1.group(1);
                }
                url = decryptUrl(url, iv);
            }else{
                SpiderDebug.log("方式1 匹配链接失败");
            }
        }
        return Result.get().url(url).header(getHeaders()).string();
    }

    public String decryptUrl(String encryptedData, String iv) {
        try {
            String encryptedKey = "VNF9aVQF!G*0ux@2hAigUeH3";

            return AESEncryption.decrypt(encryptedData, iv, "", AESEncryption.ECB_PKCS_7_PADDING);
        } catch (Exception e) {
            e.printStackTrace();
            return "123456";
        }
    }
}
