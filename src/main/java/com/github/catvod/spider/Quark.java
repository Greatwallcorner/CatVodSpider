package com.github.catvod.spider;


import com.github.catvod.api.QuarkApi;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.quark.ShareData;
import com.github.catvod.crawler.Spider;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ColaMint & Adam & FongMi
 */
public class Quark extends Spider {
    public static final String patternQuark = "(https:\\/\\/pan\\.quark\\.cn\\/s\\/[^\"]+)";


    @Override
    public void init(String extend) throws Exception {

        QuarkApi.get().setCookie(extend);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {

        ShareData shareData = QuarkApi.get().getShareData(ids.get(0));
        return Result.string(QuarkApi.get().getVod(shareData));
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return QuarkApi.get().playerContent(id.split("\\+\\+"), flag);

    }

    /**
     * 獲取詳情內容視頻播放來源（多 shared_link）
     *
     * @param ids share_link 集合
     * @return 詳情內容視頻播放來源
     */
    public String detailContentVodPlayFrom(List<String> ids) {
        List<String> playFrom = new ArrayList<>();
       /* if (ids.size() < 2){
            return TextUtils.join("$$$",  QuarkApi.get().getPlayFormatList());
        }*/
        for (int i = 1; i <= ids.size(); i++) {
            for (String s : QuarkApi.get().getPlayFormatList()) {
                playFrom.add(String.format(Locale.getDefault(), "quark" + s + "#%02d", i));

            }

        }
        return StringUtils.join(playFrom, "$$$");
    }

    /**
     * 獲取詳情內容視頻播放地址（多 share_link）
     *
     * @param ids share_link 集合
     * @return 詳情內容視頻播放地址
     */
    public String detailContentVodPlayUrl(List<String> ids) throws Exception {
        List<String> playUrl = new ArrayList<>();
        for (String id : ids) {
            ShareData shareData = QuarkApi.get().getShareData(id);
            playUrl.add(QuarkApi.get().getVod(shareData).getVodPlayUrl());
        }
        return StringUtils.join(playUrl, "$$$");
    }

    public static Object[] proxy(Map<String, String> params) throws Exception {
        String type = params.get("type");
        if ("video".equals(type)) return QuarkApi.get().proxyVideo(params);
        //if ("sub".equals(type)) return AliYun.get().proxySub(params);
        return null;
    }
}
