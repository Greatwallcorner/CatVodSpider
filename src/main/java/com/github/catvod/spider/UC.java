package com.github.catvod.spider;


import com.github.catvod.api.UCApi;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.uc.ShareData;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ColaMint & Adam & FongMi
 */
public class UC extends Spider {


    public static final String patternUC = "(https:\\/\\/drive\\.uc\\.cn\\/s\\/[^\"]+)";
    ;

    @Override
    public void init(String extend) throws Exception {

        UCApi.get().setCookie(extend);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {

        ShareData shareData = UCApi.get().getShareData(ids.get(0));
        return Result.string(UCApi.get().getVod(shareData));
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        String playContent = UCApi.get().playerContent(id.split("\\+\\+"), flag);
        SpiderDebug.log("playContent:" + playContent);
        return playContent;
    }

    /**
     * 獲取詳情內容視頻播放來源（多 shared_link）
     *
     * @param ids share_link 集合
     * @return 詳情內容視頻播放來源
     */
    public String detailContentVodPlayFrom(List<String> ids, int index) {
        List<String> playFrom = new ArrayList<>();
       /* if (ids.size() < 2){
            return TextUtils.join("$$$",  UCApi.get().getPlayFormatList());
        }*/

        for (int i = 1; i <= ids.size(); i++) {

            for (String s : UCApi.get().getPlayFormatList()) {
                playFrom.add(String.format(Locale.getDefault(), "uc" + s + "#%02d%02d", i, index));

            }
            playFrom.add("uc原画" + i + index);
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
            ShareData shareData = UCApi.get().getShareData(id);
            playUrl.add(UCApi.get().getVod(shareData).getVodPlayUrl());
        }
        return StringUtils.join(playUrl, "$$$");
    }

    public static Object[] proxy(Map<String, String> params) throws Exception {
        String type = params.get("type");
        if ("video".equals(type)) return UCApi.get().proxyVideo(params);
        //if ("sub".equals(type)) return AliYun.get().proxySub(params);
        return null;
    }
}
