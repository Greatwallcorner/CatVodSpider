package com.github.catvod.spider;


import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ColaMint & Adam & FongMi
 */
public class Cloud extends Spider {
    private Quark quark = null;
    private Ali ali = null;

    @Override
    public void init( String extend) throws Exception {
        JsonObject ext = Json.safeObject(extend);
        quark = new Quark();
        ali = new Ali();
        quark.init(context, ext.has("cookie") ? ext.get("cookie").getAsString() : "");
        ali.init(context, ext.has("token") ? ext.get("token").getAsString() : "");
    }

    @Override
    public String detailContent(List<String> shareUrl) throws Exception {
        if (shareUrl.get(0).matches(Util.patternAli)) {
            return ali.detailContent(shareUrl);
        } else if (shareUrl.get(0).matches(Util.patternQuark)) {
            return quark.detailContent(shareUrl);
        }
        return null;
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return flag.contains("quark") ? quark.playerContent(flag, id, vipFlags) : ali.playerContent(flag, id, vipFlags);
    }

    protected String detailContentVodPlayFrom(List<String> shareLinks) {
        List<String> from = new ArrayList<>();
        List<String> aliShare = new ArrayList<>();
        List<String> quarkShare = new ArrayList<>();
        for (String shareLink : shareLinks) {
            if (shareLink.matches(Util.patternAli)) {
                aliShare.add(shareLink);
            } else if (shareLink.matches(Util.patternQuark)) {
                quarkShare.add(shareLink);
            }
        }
        if (!quarkShare.isEmpty()) {
            from.add(quark.detailContentVodPlayFrom(quarkShare));
        }
        if (!aliShare.isEmpty()) {
            from.add(ali.detailContentVodPlayFrom(aliShare));
        }

        return TextUtils.join("$$$", from);
    }

    protected String detailContentVodPlayUrl(List<String> shareLinks) throws Exception {
        List<String> urls = new ArrayList<>();
        for (String shareLink : shareLinks) {
            if (shareLink.matches(Util.patternAli)) {
                urls.add(ali.detailContentVodPlayUrl(List.of(shareLink)));
            } else if (shareLink.matches(Util.patternQuark)) {
                urls.add(quark.detailContentVodPlayUrl(List.of(shareLink)));
            }
        }
        return TextUtils.join("$$$", urls);
    }
}
