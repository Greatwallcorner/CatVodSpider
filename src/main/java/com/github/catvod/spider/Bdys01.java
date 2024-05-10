package com.github.catvod.spider;

import cn.hutool.core.compress.Gzip;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.catvod.bean.Result;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.google.common.collect.Lists;
import okhttp3.Response;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.Security;
import java.util.*;


public class Bdys01 extends Spider {
    private static final String siteUrl = "https://www.yjys.me";
    private static final String siteHost = "www.yjys.me";
    private String cookie = "";
    private String referer = "";
    private String rspurl = "";


    /**
     * 筛选配置
     */
    private JSONObject filterConfig = new JSONObject("{\"0\":[{\"key\":\"s\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"all\"},{\"n\":\"动作\",\"v\":\"dongzuo\"},{\"n\":\"爱情\",\"v\":\"aiqing\"},{\"n\":\"喜剧\",\"v\":\"xiju\"},{\"n\":\"科幻\",\"v\":\"kehuan\"},{\"n\":\"恐怖\",\"v\":\"kongbu\"},{\"n\":\"战争\",\"v\":\"zhanzheng\"},{\"n\":\"武侠\",\"v\":\"wuxia\"},{\"n\":\"魔幻\",\"v\":\"mohuan\"},{\"n\":\"剧情\",\"v\":\"juqing\"},{\"n\":\"动画\",\"v\":\"donghua\"},{\"n\":\"惊悚\",\"v\":\"jingsong\"},{\"n\":\"3D\",\"v\":\"3D\"},{\"n\":\"灾难\",\"v\":\"zainan\"},{\"n\":\"悬疑\",\"v\":\"xuanyi\"},{\"n\":\"警匪\",\"v\":\"jingfei\"},{\"n\":\"文艺\",\"v\":\"wenyi\"},{\"n\":\"青春\",\"v\":\"qingchun\"},{\"n\":\"冒险\",\"v\":\"maoxian\"},{\"n\":\"犯罪\",\"v\":\"fanzui\"},{\"n\":\"纪录\",\"v\":\"jilu\"},{\"n\":\"古装\",\"v\":\"guzhuang\"},{\"n\":\"奇幻\",\"v\":\"qihuan\"},{\"n\":\"国语\",\"v\":\"guoyu\"},{\"n\":\"综艺\",\"v\":\"zongyi\"},{\"n\":\"历史\",\"v\":\"lishi\"},{\"n\":\"运动\",\"v\":\"yundong\"},{\"n\":\"原创压制\",\"v\":\"yuanchuang\"},{\"n\":\"美剧\",\"v\":\"meiju\"},{\"n\":\"韩剧\",\"v\":\"hanju\"},{\"n\":\"国产电视剧\",\"v\":\"guoju\"},{\"n\":\"日剧\",\"v\":\"riju\"},{\"n\":\"英剧\",\"v\":\"yingju\"},{\"n\":\"德剧\",\"v\":\"deju\"},{\"n\":\"俄剧\",\"v\":\"eju\"},{\"n\":\"巴剧\",\"v\":\"baju\"},{\"n\":\"加剧\",\"v\":\"jiaju\"},{\"n\":\"西剧\",\"v\":\"anish\"},{\"n\":\"意大利剧\",\"v\":\"yidaliju\"},{\"n\":\"泰剧\",\"v\":\"taiju\"},{\"n\":\"港台剧\",\"v\":\"gangtaiju\"},{\"n\":\"法剧\",\"v\":\"faju\"},{\"n\":\"澳剧\",\"v\":\"aoju\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"不限\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"澳大利亚\",\"v\":\"澳大利亚\"},{\"n\":\"比利时\",\"v\":\"比利时\"},{\"n\":\"瑞典\",\"v\":\"瑞典\"},{\"n\":\"荷兰\",\"v\":\"荷兰\"},{\"n\":\"丹麦\",\"v\":\"丹麦\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"豆瓣评分\",\"v\":\"1\"}]}],\"1\":[{\"key\":\"s\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"all\"},{\"n\":\"动作\",\"v\":\"dongzuo\"},{\"n\":\"爱情\",\"v\":\"aiqing\"},{\"n\":\"喜剧\",\"v\":\"xiju\"},{\"n\":\"科幻\",\"v\":\"kehuan\"},{\"n\":\"恐怖\",\"v\":\"kongbu\"},{\"n\":\"战争\",\"v\":\"zhanzheng\"},{\"n\":\"武侠\",\"v\":\"wuxia\"},{\"n\":\"魔幻\",\"v\":\"mohuan\"},{\"n\":\"剧情\",\"v\":\"juqing\"},{\"n\":\"动画\",\"v\":\"donghua\"},{\"n\":\"惊悚\",\"v\":\"jingsong\"},{\"n\":\"3D\",\"v\":\"3D\"},{\"n\":\"灾难\",\"v\":\"zainan\"},{\"n\":\"悬疑\",\"v\":\"xuanyi\"},{\"n\":\"警匪\",\"v\":\"jingfei\"},{\"n\":\"文艺\",\"v\":\"wenyi\"},{\"n\":\"青春\",\"v\":\"qingchun\"},{\"n\":\"冒险\",\"v\":\"maoxian\"},{\"n\":\"犯罪\",\"v\":\"fanzui\"},{\"n\":\"纪录\",\"v\":\"jilu\"},{\"n\":\"古装\",\"v\":\"guzhuang\"},{\"n\":\"奇幻\",\"v\":\"qihuan\"},{\"n\":\"国语\",\"v\":\"guoyu\"},{\"n\":\"综艺\",\"v\":\"zongyi\"},{\"n\":\"历史\",\"v\":\"lishi\"},{\"n\":\"运动\",\"v\":\"yundong\"},{\"n\":\"原创压制\",\"v\":\"yuanchuang\"},{\"n\":\"美剧\",\"v\":\"meiju\"},{\"n\":\"韩剧\",\"v\":\"hanju\"},{\"n\":\"国产电视剧\",\"v\":\"guoju\"},{\"n\":\"日剧\",\"v\":\"riju\"},{\"n\":\"英剧\",\"v\":\"yingju\"},{\"n\":\"德剧\",\"v\":\"deju\"},{\"n\":\"俄剧\",\"v\":\"eju\"},{\"n\":\"巴剧\",\"v\":\"baju\"},{\"n\":\"加剧\",\"v\":\"jiaju\"},{\"n\":\"西剧\",\"v\":\"anish\"},{\"n\":\"意大利剧\",\"v\":\"yidaliju\"},{\"n\":\"泰剧\",\"v\":\"taiju\"},{\"n\":\"港台剧\",\"v\":\"gangtaiju\"},{\"n\":\"法剧\",\"v\":\"faju\"},{\"n\":\"澳剧\",\"v\":\"aoju\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"不限\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"澳大利亚\",\"v\":\"澳大利亚\"},{\"n\":\"比利时\",\"v\":\"比利时\"},{\"n\":\"瑞典\",\"v\":\"瑞典\"},{\"n\":\"荷兰\",\"v\":\"荷兰\"},{\"n\":\"丹麦\",\"v\":\"丹麦\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"豆瓣评分\",\"v\":\"1\"}]}],\"a\":[{\"key\":\"s\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"all\"},{\"n\":\"动作\",\"v\":\"dongzuo\"},{\"n\":\"爱情\",\"v\":\"aiqing\"},{\"n\":\"喜剧\",\"v\":\"xiju\"},{\"n\":\"科幻\",\"v\":\"kehuan\"},{\"n\":\"恐怖\",\"v\":\"kongbu\"},{\"n\":\"战争\",\"v\":\"zhanzheng\"},{\"n\":\"武侠\",\"v\":\"wuxia\"},{\"n\":\"魔幻\",\"v\":\"mohuan\"},{\"n\":\"剧情\",\"v\":\"juqing\"},{\"n\":\"动画\",\"v\":\"donghua\"},{\"n\":\"惊悚\",\"v\":\"jingsong\"},{\"n\":\"3D\",\"v\":\"3D\"},{\"n\":\"灾难\",\"v\":\"zainan\"},{\"n\":\"悬疑\",\"v\":\"xuanyi\"},{\"n\":\"警匪\",\"v\":\"jingfei\"},{\"n\":\"文艺\",\"v\":\"wenyi\"},{\"n\":\"青春\",\"v\":\"qingchun\"},{\"n\":\"冒险\",\"v\":\"maoxian\"},{\"n\":\"犯罪\",\"v\":\"fanzui\"},{\"n\":\"纪录\",\"v\":\"jilu\"},{\"n\":\"古装\",\"v\":\"guzhuang\"},{\"n\":\"奇幻\",\"v\":\"qihuan\"},{\"n\":\"国语\",\"v\":\"guoyu\"},{\"n\":\"综艺\",\"v\":\"zongyi\"},{\"n\":\"历史\",\"v\":\"lishi\"},{\"n\":\"运动\",\"v\":\"yundong\"},{\"n\":\"原创压制\",\"v\":\"yuanchuang\"},{\"n\":\"美剧\",\"v\":\"meiju\"},{\"n\":\"韩剧\",\"v\":\"hanju\"},{\"n\":\"国产电视剧\",\"v\":\"guoju\"},{\"n\":\"日剧\",\"v\":\"riju\"},{\"n\":\"英剧\",\"v\":\"yingju\"},{\"n\":\"德剧\",\"v\":\"deju\"},{\"n\":\"俄剧\",\"v\":\"eju\"},{\"n\":\"巴剧\",\"v\":\"baju\"},{\"n\":\"加剧\",\"v\":\"jiaju\"},{\"n\":\"西剧\",\"v\":\"anish\"},{\"n\":\"意大利剧\",\"v\":\"yidaliju\"},{\"n\":\"泰剧\",\"v\":\"taiju\"},{\"n\":\"港台剧\",\"v\":\"gangtaiju\"},{\"n\":\"法剧\",\"v\":\"faju\"},{\"n\":\"澳剧\",\"v\":\"aoju\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"不限\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"澳大利亚\",\"v\":\"澳大利亚\"},{\"n\":\"比利时\",\"v\":\"比利时\"},{\"n\":\"瑞典\",\"v\":\"瑞典\"},{\"n\":\"荷兰\",\"v\":\"荷兰\"},{\"n\":\"丹麦\",\"v\":\"丹麦\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"更新时间\",\"v\":\"0\"},{\"n\":\"豆瓣评分\",\"v\":\"1\"}]}]}");
    ;


   /* @Override
    public void init() throws Exception {
        super.init();
        try {
            filterConfig
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }*/

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url, String ref) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        if (!ref.equals("google")) {
            headers.put("Authority", "www.bdys01.com");
            if (ref.length() > 0) {
                if (ref.equals("origin")) {
                    headers.put("Origin", "https://www.bdys01.com");
                } else {
                    headers.put("Referer", ref);
                }
            }
            if (cookie.length() > 0) {
                headers.put("Cookie", cookie);
            }
        }
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }

    protected HashMap<String, String> getHeaders2(String url) {
        HashMap<String, String> headers = new HashMap<>();
        String ss = url.replace("https://", "").split("/")[0];
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Authority", ss);
        headers.put("Origin", "www.bdys01.com");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }


    protected void getCookie() {
        cookie = "";
        String cookieurl = "https://www.bdys01.com/zzzzz";
        Map<String, List<String>> cookies = new HashMap<>();
        OkHttp.string(cookieurl, getHeaders(cookieurl, ""));
        for (Map.Entry<String, List<String>> entry : cookies.entrySet()) {
            if (entry.getKey().equals("set-cookie")) {
                cookie = StrUtil.join(";", entry.getValue());
                break;
            }
        }
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            getCookie();
            Document doc = Jsoup.parse(OkHttp.string(siteUrl, getHeaders(siteUrl, referer)));
            referer = siteUrl + "/";
            // 分类节点
            JSONObject result = new JSONObject();
            JSONArray classes = new JSONArray();
            String catestr = "{\"全部\": \"a\",\"电影\": \"0\",\"电视剧\": \"1\"}";
            JSONObject catedef = new JSONObject(catestr);
            Iterator it = catedef.keys();
            while (it.hasNext()) {
                JSONObject jsonObject = new JSONObject();
                String key = (String) it.next();
                jsonObject.put("type_name", key);
                jsonObject.put("type_id", catedef.getString(key));
                classes.put(jsonObject);
            }
            result.put("class", classes);
            if (filter) {
                result.put("filters", filterConfig);
            }
            try {
                // 取首页推荐视频列表
                Element homeList = doc.select("div.row.row-cards").get(0);
                Elements list = homeList.select("div.col-4.rows-md-7");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("h3.card-title").text();
                    String cover = vod.selectFirst("img.w-100").attr("data-src");
                    String remark = vod.selectFirst("p.text-muted").text();
                    String id = vod.selectFirst("a.d-block.cover").attr("href");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+homeContent"+result.toString());
            return result.toString();

        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
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
        try {
            String url = siteUrl + "/s/";
            if (extend != null && extend.size() > 0) {
                String urlformat = "";
                if (tid.equals("a")) {
                    urlformat = url + "{s}/" + pg + "?&area={area}&year={year}&order={order}";
                } else {
                    urlformat = url + "{s}/" + pg + "?&type=" + tid + "&area={area}&year={year}&order={order}";
                }
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value.length() > 0) {
                        urlformat = urlformat.replace("{" + key + "}", URLEncoder.encode(value));
                    }
                }
                for (int i = 0; i < 4; i++) {
                    if (urlformat.contains("{s}")) {
                        urlformat = urlformat.replace("{s}", "all");
                    } else if (urlformat.contains("{area}")) {
                        urlformat = urlformat.replace("&area={area}", "");
                    } else if (urlformat.contains("{year}")) {
                        urlformat = urlformat.replace("&year={year}", "");
                    } else if (urlformat.contains("{order}")) {
                        urlformat = urlformat.replace("{order}", "0");
                    }
                }
                url = urlformat;
            } else {
                if (tid.equals("a")) {
                    url += "all/" + pg + "?&order=0";
                } else {
                    url += "all/" + pg + "?&type=" + tid + "&order=0";
                }
            }
            referer = siteUrl + "/";
            String html = OkHttp.string(url, getHeaders(url, referer));
            referer = url;
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("div.col-lg-8");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("h3.mb-0").text();
                    String cover = vod.selectFirst("img.w-100").attr("src");
                    String remark = vod.selectFirst("p.mb-0").text();
                    String idtt = vod.selectFirst("a.d-block").attr("href");
                    String id = "";
                    if (idtt.contains("JSESSIONID")) {
                        int end = idtt.indexOf(";");
                        id = idtt.substring(0, end);
                    } else {
                        id = idtt;
                    }
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
            }
            int page = Integer.parseInt(pg);
            result.put("page", page);
            result.put("pagecount", videos.length() == 24 ? page + 1 : page);
            result.put("limit", 24);
            result.put("total", Integer.MAX_VALUE);
            result.put("list", videos);
            SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+categoryContent"+result.toString());

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
            String url = siteUrl + ids.get(0);
            Document doc = Jsoup.parse(OkHttp.string(url, getHeaders(url, referer)));
            referer = url;
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();
            String cover = doc.selectFirst("div.col-md-auto img").attr("src");
            String title = doc.selectFirst("h1.d-none.d-md-block").text();
            String desc = doc.select("div.card.collapse > div.card-body").text();
            String year = doc.select("span.badge.bg-purple-lt").text().replace("更新时间：", "").split("-")[0];
            String category = "", area = "", remark = "", director = "", actor = "";
            Element details = doc.select("div.col.mb-2").get(0);
            Elements allp = details.select("p");
            for (int i = 0; i < allp.size(); i++) {
                Element text = allp.get(i);
                String info = text.selectFirst("strong").text();
                if (info.equals("类型：")) {
                    List<String> categorys = new ArrayList<>();
                    Elements aa = text.select("a");
                    for (int j = 0; j < aa.size(); j++) {
                        categorys.add(aa.get(j).text());
                    }
                    category = "类型：" + StrUtil.join(",", categorys);
                } else if (info.contains("地区")) {
                    int start = text.text().indexOf("[") + 1;
                    int end = text.text().indexOf("]");
                    area = text.text().substring(start, end);
                } else if (info.contains("豆瓣")) {
                    remark = "豆瓣:" + text.text();
                } else if (info.contains("导演：")) {
                    director = text.selectFirst("a").text();
                } else if (info.contains("主演：")) {
                    List<String> actors = new ArrayList<>();
                    Elements aa = text.select("a");
                    for (int j = 0; j < aa.size(); j++) {
                        actors.add(aa.get(j).text());
                    }
                    actor = StrUtil.join(",", actors);
                }
            }
            String vid = ids.get(0);
            vodList.put("vod_id", vid);
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_remarks", remark);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);
            Map<String, String> vod_play = new TreeMap<>();
            // 取播放列表数据
            Elements playListA = doc.select("a.btn.btn-square");
            String sourceName = "播放列表";
            String playList = "";
            List<String> vodItems = new ArrayList<>();
            for (int j = 0; j < playListA.size(); j++) {
                Element vod = playListA.get(j);
                String idtt = vod.attr("href");
                String playURL = "";
                if (idtt.contains("JSESSIONID")) {
                    int end = idtt.indexOf(";");
                    playURL = idtt.substring(0, end);
                } else {
                    playURL = idtt;
                }
                vodItems.add(vod.text() + "$" + playURL);
            }
            if (vodItems.size() > 0) playList = StrUtil.join("#", vodItems);
            vod_play.put(sourceName, playList);
            if (vod_play.size() > 0) {
                String vod_play_from = StrUtil.join("$$$", vod_play.keySet());
                String vod_play_url = StrUtil.join("$$$", vod_play.values());
                vodList.put("vod_play_from", vod_play_from);
                vodList.put("vod_play_url", vod_play_url);
            }
            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+detailContent"+result.toString());
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
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
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            String url = siteUrl + id;
            String htmlplay = OkHttp.string(url, getHeaders(url, referer));
            Document doc = Jsoup.parse(htmlplay);
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            String pid = "";
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).toString();
                if (scContent.contains("var pid =")) {
                    int start = scContent.indexOf("var pid =");
                    int end = scContent.lastIndexOf("var time") + 1;
                    String pidtmp = scContent.substring(start, end);
                    start = pidtmp.indexOf("=") + 2;
                    end = pidtmp.lastIndexOf(";");
                    pid = pidtmp.substring(start, end);
                    break;
                }
            }
            long t = System.currentTimeMillis();
            String key = SecureUtil.md5(pid + "-" + t).substring(0, 16);
            String sg = encrypt(pid + "-" + t, key);
            String geturl = siteUrl + "/lines?t=" + t + "&sg=" + sg + "&pid=" + pid;
            String urlsrc = OkHttp.string(geturl, getHeaders(geturl, ""));
            JSONObject urlsrcobj = new JSONObject(urlsrc);
            JSONObject urldb = urlsrcobj.getJSONObject("data");
            List<String> urldblist = new ArrayList<>();
            String realurl = "";
            rspurl = "";
            if (!urldb.isNull("tos")) {
                String tos = urldb.optString("tos");
                if (tos.equals("1")) {
                    t = System.currentTimeMillis();
                    key = SecureUtil.md5(pid + "-" + t).substring(0, 16);
                    sg = encrypt(pid + "-" + t, key);
                    geturl = siteUrl + "/god/" + pid + "?type=1";
                    HashMap<String, String> hashMap2 = new HashMap();
                    hashMap2.put("t", Long.toString(t));
                    hashMap2.put("sg", sg);
                    hashMap2.put("verifyCode", "888");
                    OkResult result1 = OkHttp.post(OkHttp.client(), geturl, hashMap2, getHeaders(geturl, "origin"));

                    rspurl = new JSONObject(result1.getBody()).getString("url");

                    if (rspurl.length() > 0) {
                        realurl = rspurl;
                        result.put("parse", 0);
                        result.put("playUrl", "");
                        result.put("url", realurl);
                        result.put("header", "");
                        SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+playerContent"+Result.get().url(realurl).header(getHeaders(geturl, "")).string());
                        return Result.get().url(realurl).header(getHeaders(geturl, "")).string();
                    }
                }
            }
            if (!urldb.isNull("url3")) {
                String url3 = urldb.optString("url3");
                urldblist.add(url3);
            }
            if (!urldb.isNull("m3u8")) {
                String m3u8 = urldb.optString("m3u8").replace("www.bde4.cc", "www.bdys01.com");
                urldblist.add(m3u8);
            }
            if (!urldb.isNull("m3u8_2")) {
                String m3u8_2 = urldb.optString("m3u8_2");
                String[] m2 = m3u8_2.split(",");
                for (String s : m2) {
                    urldblist.add(s.replace("www.bde4.cc", "www.bdys01.com"));
                }
            }
            if (urldblist.isEmpty()) {
                t = System.currentTimeMillis();
                key = SecureUtil.md5(pid + "-" + t).substring(0, 16);
                sg = encrypt(pid + "-" + t, key);
                geturl = siteUrl + "/god/" + pid;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("t", Long.toString(t));
                hashMap.put("sg", sg);
                hashMap.put("verifyCode", "666");
                OkResult result2 = OkHttp.post(OkHttp.client(), geturl, hashMap, getHeaders(geturl, "origin"));

                rspurl = new JSONObject(result2.getBody()).getString("url");

                if (rspurl.contains("rkey")) {
                    t = System.currentTimeMillis();
                    realurl = rspurl.replace("?rkey", t + ".mp4?ver=6010&rkey");
                } else {
                    t = System.currentTimeMillis();
                    realurl = rspurl.replace("http:", "https:") + "/" + t + ".mp4";
                }
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", realurl);
                result.put("header", "");
                SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+playerContent"+result.toString());
                Result.get().url(realurl).header(getHeaders(geturl, "")).string();
            }
            int index = new Random().nextInt(urldblist.size());
            String videourl = urldblist.get(index);
            if (videourl.contains("mp4")) {
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", videourl);
                result.put("header", "");
                SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+playerContent"+result.toString());
                return Result.get().url(realurl).header(getHeaders(geturl, "")).string();
            }
            Map<String, List<String>> respHeaders = new HashMap<>();
            String redirect = OkHttp.getLocation(videourl, getHeaders(videourl, ""));

            String realm3u8 = "";

            Response okResult = OkHttp.newCall(redirect, getHeaders2(redirect));
            if (okResult.code() == 200) {
                InputStream picsrc = okResult.body().byteStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[4];
                while ((nRead = picsrc.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] targetArray = buffer.toByteArray();
                picsrc.close();
                byte[] dep = Arrays.copyOfRange(targetArray, 3354, targetArray.length);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Gzip.of(new ByteArrayInputStream(dep), outputStream).unGzip();
                String pp = outputStream.toString();
                String[] m3u8Ori = pp.split("\n");
                for (int i = 0; i < m3u8Ori.length; i++) {
                    String mt = m3u8Ori[i];
                    if (mt.contains(".ts")) {
                        mt = "https://vod.bdys.me/" + mt;
                        m3u8Ori[i] = mt;
                    }
                }
                realm3u8 = StrUtil.join("\n", Lists.newArrayList(m3u8Ori));
            }
            String realm3u8b64 = Base64.getEncoder().encodeToString(realm3u8.getBytes());
            result.put("url", "data:application/vnd.apple.mpegurl;base64," + realm3u8b64);
            result.put("parse", 0);
            result.put("playUrl", "");
            result.put("header", new JSONObject(getHeaders2("https://vod.bdys.me/")).toString());
            SpiderDebug.log("!!!!!!!!!!!!!!哔嘀影视+playerContent"+result.toString());
            return Result.get().url(realurl).header(getHeaders(geturl, "")).string();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        try {
            String url = "https://www.google.com/search?q=site%3A" + siteHost + "+" + URLEncoder.encode(key);
            Document doc = Jsoup.parse(OkHttp.string(url, getHeaders(url, "google")));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            Elements sourceList = doc.select("div.yuRUbf a");
            if (!sourceList.isEmpty()) {
                for (int i = 0; i < 1; i++) {
                    Element sourcess = sourceList.get(i);
                    String sourceName = sourcess.select("h3.LC20lb.MBeuO.DKV0Md").text();
                    String list1 = sourcess.attr("href");
                    if (list1.contains("/s/") || list1.contains("play") || list1.contains("performer") || list1.contains("search") || list1.contains("jsessionid")) {
                        continue;
                    }
                    if (sourceName.contains(key)) {
                        Document ddrklink = Jsoup.parse(OkHttp.string(list1, getHeaders(list1, referer)));
                        JSONObject v = new JSONObject();
                        String cover = ddrklink.selectFirst("div.col-md-auto img").attr("src");
                        String title = ddrklink.selectFirst("h2.d-sm-block.d-md-none").text();
                        String id = list1.replace("https://www.bdys01.com", "");
                        v.put("vod_name", title);
                        v.put("vod_remarks", "");
                        v.put("vod_id", id);
                        v.put("vod_pic", cover);
                        videos.put(v);
                    }
                }
                result.put("list", videos);
                return result.toString();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    protected String encrypt(String src, String KEY) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return bytesToHexStr(cipher.doFinal(src.getBytes())).toUpperCase();
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }

    protected String bytesToHexStr(byte[] bytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }

}