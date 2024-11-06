package com.github.catvod.spider;


import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.Json;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ColaMint & Adam & FongMi
 */
public class Cloud extends Spider {
    private Quark quark = null;
    private Ali ali = null;
    private UC uc = null;

    @Override
    public void init(String extend) throws Exception {
        JsonObject ext = StringUtils.isAllBlank(extend) ? new JsonObject() : Json.safeObject(extend);
        quark = new Quark();
        ali = new Ali();
        uc = new UC();
        quark.init(ext.has("cookie") ? ext.get("cookie").getAsString() : "");
        ali.init(ext.has("token") ? ext.get("token").getAsString() : "");
        uc.init(ext.has("uccookie") ? ext.get("uccookie").getAsString() : "");

    }

    @Override
    public String detailContent(List<String> shareUrl) throws Exception {
        if (shareUrl.get(0).matches(Ali.pattern.pattern())) {
            return ali.detailContent(shareUrl);
        } else if (shareUrl.get(0).matches(Quark.patternQuark)) {
            return quark.detailContent(shareUrl);
        } else if (shareUrl.get(0).matches(UC.patternUC)) {
            return uc.detailContent(shareUrl);
        }
        return null;
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        if (flag.contains("quark")) {
            return quark.playerContent(flag, id, vipFlags);
        } else if (flag.contains("uc")) {
            return uc.playerContent(flag, id, vipFlags);
        } else {
            return ali.playerContent(flag, id, vipFlags);
        }
    }

    protected String detailContentVodPlayFrom(List<String> shareLinks) {
        List<String> from = new ArrayList<>();
        List<String> aliShare = new ArrayList<>();
        List<String> quarkShare = new ArrayList<>();
        List<String> ucShare = new ArrayList<>();

        for (String shareLink : shareLinks) {
            if (shareLink.matches(Ali.pattern.pattern())) {
                aliShare.add(shareLink);
            } else if (shareLink.matches(Quark.patternQuark)) {
                quarkShare.add(shareLink);
            } else if (shareLink.matches(UC.patternUC)) {
                ucShare.add(shareLink);
            }
        }
        if (!ucShare.isEmpty()) {
            from.add(uc.detailContentVodPlayFrom(ucShare));
        }
        if (!quarkShare.isEmpty()) {
            from.add(quark.detailContentVodPlayFrom(quarkShare));
        }
        if (!aliShare.isEmpty()) {
            from.add(ali.detailContentVodPlayFrom(aliShare));
        }

        return StringUtils.join(from, "$$$");
    }

    protected String detailContentVodPlayUrl(List<String> shareLinks) throws Exception {
        List<String> urls = new ArrayList<>();
        for (String shareLink : shareLinks) {
            if (shareLink.matches(Ali.pattern.pattern())) {
                urls.add(ali.detailContentVodPlayUrl(ImmutableList.of(shareLink)));
            } else if (shareLink.matches(Quark.patternQuark)) {
                urls.add(quark.detailContentVodPlayUrl(ImmutableList.of(shareLink)));
            } else if (shareLink.matches(UC.patternUC)) {
                urls.add(uc.detailContentVodPlayUrl(ImmutableList.of(shareLink)));
            }
        }
        return StringUtils.join(urls, "$$$");
    }
}
