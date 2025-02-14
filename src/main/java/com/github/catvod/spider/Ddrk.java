package com.github.catvod.spider;

import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.ProxyVideo;
import com.github.catvod.utils.Utils;
import com.google.gson.JsonElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Demo for self study
 * <p>
 * Source from Author: CatVod
 */

public class Ddrk extends Cloud {

    private static String siteUrl = "https://ddys.pro";


    protected JSONObject filterConfig;

    protected Pattern regexCategory = Pattern.compile("/category/(\\S+)/");
    protected Pattern regexVid = Pattern.compile("https://ddys.pro/(\\S+)/");

    protected Pattern regexPage = Pattern.compile("\\S+/page/(\\S+)\\S+");
    protected Pattern m = Pattern.compile("\\S+(http\\S+g)");
    protected Pattern mark = Pattern.compile("\\S+(.*)");

    //   protected Pattern t = Pattern.compile("(\\S+)");

    protected static HashMap<String, String> Headers() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Referer", siteUrl);
        return headers;
    }

    private static String doReplaceRegex(Pattern pattern, String src) {
        if (pattern == null) return src;
        try {
            Matcher matcher = pattern.matcher(src);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return src;
    }

    @Override
    public void init(String extend) throws Exception {
        super.init(extend);
        JsonElement json = Json.parse(extend);
        String html = OkHttp.string(json.getAsJsonObject().get("site").getAsString());
        Document doc = Jsoup.parse(html);
        for (Element element : doc.select("a")) {
            if (element.text().contains("https")) {
                siteUrl = element.attr("href");
                break;
            }
        }
        SpiderDebug.log("ddys =====>" + siteUrl); // js_debug.log

    }

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        headers.put("Referer", siteUrl);
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {

        List<Vod> vods = new ArrayList<>();
        String url = siteUrl + '/';
        Document doc = Jsoup.parse(OkHttp.string(url, getHeaders(url)));
        Elements elements = doc.select("li.menu-item a");
        List<Class> classes = new ArrayList<>();

        for (Element ele : elements) {
            String name = ele.attr("title");
            String id = ele.attr("href");
            if (ele.attr("href").contains("category") || ele.attr("href").contains("tag")) {
                classes.add(new Class(id, name));
            }
        }


        // 取首页推荐视频列表
        Elements list = doc.select("div.post-box-container");

        for (int i = 0; i < list.size(); i++) {
            Element vod = list.get(i);
            String title = vod.selectFirst(".post-box-title > a").text();
            String id = vod.selectFirst(".post-box-title > a").attr("href");
            String imageHtml = vod.selectFirst("div.post-box-image").attr("style");
            String image = "";
            String regex = "url\\((.*?)\\)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(imageHtml);
            if (matcher.find()) {
                image = matcher.group(1);
            }
            vods.add(new Vod(id, title, image));
        }


        return Result.string(classes, vods, filterConfig);
    }

    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        String url = "";
        try {
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value != null && value.length() != 0 && value != " ") {
                        url = siteUrl + "/category/" + tid + "/" + value;
                    } else {
                        url = siteUrl + "/category/" + tid;
                    }
                }
            } else {
                url = tid;
            }
            if (pg.equals("1")) {
                url = url + "/";
            } else {
                url = url + "/page/" + pg + "/";
            }
            //System.out.println(url);
            String html = OkHttp.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            // 取页码相关信息
            Elements pageInfo = doc.select("div.nav-links");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null) continue;
                    String wy = doc.select("div.nav-links a").last().attr("href");
                    String span = doc.select("span.current").text().trim();
                    if (page == -1) {
                        page = Integer.parseInt(span);
                    } else {

                        page = 0;
                    }
                    Matcher matcher = regexPage.matcher(wy);
                    if (matcher.find()) {
                        //System.out.println("尾页" + matcher.group(1));
                        pageCount = Integer.parseInt(matcher.group(1));
                    } else {
                        pageCount = 0;
                    }
                    break;
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("div.post-box-container");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String a = vod.selectFirst(".post-box-title a").text();
                    if (a.contains("(")) {
                        String[] item = a.split("\\(");
                        String title = item[0];
                        String remark = item[1].replace(")", "");
                        String cover = doReplaceRegex(m, vod.selectFirst(".post-box-image").attr("style"));

                        String id = vod.selectFirst(".post-box-title a").attr("href");
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    } else {
                        String title = a;
                        String cover = doReplaceRegex(m, vod.selectFirst(".post-box-image").attr("style"));
                        String remark = doReplaceRegex(mark, vod.selectFirst(".post-box-title a").text());
                        Matcher matcher = regexVid.matcher(vod.selectFirst(".post-box-title a").attr("href"));
                        if (!matcher.find()) continue;
                        String id = matcher.group(1);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    }
                }
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 24);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 24);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
    @Override
    public String detailContent(List<String> ids) {
        try {
            // 视频详情url
            String url = ids.get(0);
            if (!url.startsWith("http")) {
                url = siteUrl +"/"+ url + "/";
            }
            Document doc = Jsoup.parse(OkHttp.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.select("div.post img").attr("src");
            String ab = doc.select("h1.post-title").text();
            if (ab.contains("(")) {
                String[] b = ab.split("\\(");
                String title = b[0];
                String remark = b[1].replace("(", "");
                vodList.put("vod_name", title);
                vodList.put("vod_remarks", remark);
            } else {
                vodList.put("vod_name", ab);
                String remark = doc.select("time").text().trim();
                vodList.put("vod_remarks", "全");
            }
            String str2 = doc.select("div.abstract").text().replace(" ", "");
            String replace = str2.replace("<br>", "");
            String text = replace.replace("<p></p>", "");
            Pattern categorys = Pattern.compile("类型:(.*)制");
            String category = doReplaceRegex(categorys, text);
            Pattern a = Pattern.compile("年份:(.*)简");
            String year = doReplaceRegex(a, text);
            Pattern b = Pattern.compile("地区:(.*)年份");
            String area = doReplaceRegex(b, text);
            Pattern c = Pattern.compile("演员:(.*)类");
            String actor = doReplaceRegex(c, text);
            Pattern d = Pattern.compile("导演:(.*)演");
            String director = doReplaceRegex(d, text);
            Pattern e = Pattern.compile("简介:(.*)");
            String desc = doReplaceRegex(e, text);


            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);
            Vod.VodPlayBuilder builder = new Vod.VodPlayBuilder();
            List<String> shareLinks = new ArrayList<>();
            getVodDetail(doc, builder, "1");

            //多季剧集处理
            Elements sources = doc.select(".post-page-numbers");
            if (!sources.isEmpty()) {
                for (Element source : sources) {
                    if (!source.select("a").isEmpty()) {

                        String Purl = source.select("a").attr("href");
                        Document docs = Jsoup.parse(OkHttp.string(Purl, getHeaders(Purl)));
                        getVodDetail(docs, builder, source.text());
                    }
                }
            }

            Elements clouds = doc.select("p > a");
            if (!clouds.isEmpty()) {
                for (Element cloud : clouds) {
                    String cloudUrl = cloud.attr("href");
                    if (!Utils.findByRegex(Utils.patternQuark, cloudUrl, 0).isBlank() || !Utils.findByRegex(Utils.patternUC, cloudUrl, 0).isBlank()) {
                        shareLinks.add(cloudUrl);
                    }

                }
            }

            String quarkNames = "";
            String quarkUrls = "";
            if (!shareLinks.isEmpty()) {
                quarkUrls = super.detailContentVodPlayUrl(shareLinks);
                quarkNames = super.detailContentVodPlayFrom(shareLinks);

            }

            Vod.VodPlayBuilder.BuildResult buildResult = builder.build();
            vodList.put("vod_play_from", buildResult.vodPlayFrom + "$$$" + quarkNames);
            vodList.put("vod_play_url", buildResult.vodPlayUrl + "$$$" + quarkUrls);

            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            return result.toString();

        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    private void getVodDetail(Document doc, Vod.VodPlayBuilder builder, String index) throws JSONException {
        Elements allScript = doc.select(".wp-playlist-script");
        String sourceName = "第" + index + "季";
        for (Element element : allScript) {
            String scContent = element.html().trim();
            int start = scContent.indexOf('{');
            int end = scContent.lastIndexOf('}') + 1;
            String json = scContent.substring(start, end);
            JSONObject UJson = new JSONObject(json);
            JSONArray Track = UJson.getJSONArray("tracks");
            List<Vod.VodPlayBuilder.PlayUrl> list = new ArrayList<>();
            for (int k = 0; k < Track.length(); k++) {
                JSONObject src = Track.getJSONObject(k);
                String adk = src.getString("src0");
                String vodName = src.getString("caption");
                String pzm = getPlayUrl(adk);
                Vod.VodPlayBuilder.PlayUrl playUrl = new Vod.VodPlayBuilder.PlayUrl();
                playUrl.name = vodName;
                playUrl.url = ProxyVideo.buildCommonProxyUrl(pzm, Utils.webHeaders(siteUrl));
                list.add(playUrl);
            }
            builder.append(sourceName, list);
        }
    }

    public String getPlayUrl(String source) {
        if (source.endsWith("m3u8")) {
            return source;
        } else if (source.startsWith("https")) {
            return source;
        } else {
            return "https://v.ddys.pro" + source;
        }
    }

    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        if (flag.contains("quark")||flag.contains("uc")) {
            return super.playerContent(flag, id, vipFlags);
        } else {
            return Result.get().url(ProxyVideo.buildCommonProxyUrl(id, Utils.webHeaders(siteUrl))).string();
        }
    }

    @Override
    public String searchContent(String key, boolean quick) {

        String url = siteUrl + "?s=" + URLEncoder.encode(key) + "&post_type=post";
        Document doc = Jsoup.parse(OkHttp.string(url, getHeaders(url)));
        List<Vod> vods = new ArrayList<>();
        Elements elements = doc.select("h2.post-title > a");
        for (int i = 0; i < elements.size(); i++) {
            String id = elements.get(i).attr("href");
            String name = elements.get(i).text();
            vods.add(new Vod(id, name, ""));
        }
        return Result.string(vods);
    }


}