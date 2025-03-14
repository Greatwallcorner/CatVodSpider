package com.github.catvod.spider;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Util;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class Yingso extends Ali {
    private static final String siteUrl = Util.base64Decode("aHR0cHM6Ly95aW5nc28uZnVuOjMwMDEv");

    private static final String searchUrl = Util.base64Decode("aHR0cHM6Ly95cy5hcGkueWluZ3NvLmZ1bi92My9hbGkvc2VhcmNo");

    private static final String shareUrl = "https://www.aliyundrive.com/s/";

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String data = OkHttp.post(searchUrl, new Req(30, 1, key).toJson(), Util.webHeaders(siteUrl)).getBody();
        Res res = Res.fromJson(data);
        if (res == null || res.code != 200) {
            SpiderDebug.log("yingso error:" + res.msg + "data: " + data);
            return "";
        }
        return Result.string(res.toVodList());
    }

    // {"pageSize":30,"pageNum":1,"title":"繁花","root":0}
    class Req {
        Integer pageSize;
        Integer pageNum;

        String title;

        // ？ 不知道这是啥 层级？
        Integer root = 0;

        String cat = "all";

        public Req(Integer pageSize, Integer pageNum, String title) {
            this.pageSize = pageSize;
            this.pageNum = pageNum;
            this.title = title;
        }

        public String toJson() {
            return Json.toJson(this);
        }
    }

    // {"code":200,"msg":"查询成功","data":[{"id":46788,"cat":"qt","key":"1pog1_HhV_tLM9_U5SAz5Cg?pwd=ub5x","title":"繁花 胡歌 马伊琍 4K 国沪双语 最新上线 首更4集","time":"1710314929","root":4},
    static class Res {
        Integer code;

        String msg;

        List<ResItem> data;

        public static Res fromJson(String json) {
            return Json.parseSafe(json, Res.class);
        }

        public List<Vod> toVodList() {
            if (data == null || data.isEmpty()) {
                return Lists.newArrayList();
            }
            ArrayList<Vod> list = Lists.newArrayList();
            for (ResItem item : data) {
                list.add(new Vod(shareUrl + item.key, item.title, "https://inews.gtimg.com/newsapp_bt/0/13263837859/1000",
                        ""));
            }
            return list;
        }
    }

    class ResItem {
        Integer id;

        String cat;

        String key;

        String title;

        Long time;

        Integer root;
    }
}
