package com.github.catvod.spider.unavailable;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Ali;

import java.net.URLEncoder;

public class Dovx extends Ali {

    @Override
    public String searchContent(String key, boolean quick) {
        Result result = Result.objectFrom(OkHttp.string("https://api.dovx.tk/ali/search?wd=" + URLEncoder.encode(key)));
        for (Vod vod : result.getList()) vod.setVodId(vod.getVodContent());
        return result.string();
    }
}
