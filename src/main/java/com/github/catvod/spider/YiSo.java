package com.github.catvod.spider;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

// {"code":205,"msg":"苦于爬虫、导链的困扰，本站不得不做出以下限制，未登录用户每天搜索次数将受到限制,登录用户无限制","data":null}
public class YiSo extends Ali {

    private String uuid = UUID.randomUUID().toString();

    private Integer time_205 = 0;

    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 12; V2049A Build/SP1A.210812.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/103.0.5060.129 Mobile Safari/537.36");
        headers.put("Referer", "https://yiso.fun/");
        headers.put("satoken", uuid);
        return headers;
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        String json = OkHttp.string("https://yiso.fun/api/search?name=" + URLEncoder.encode(key) + "&pageNo=1&from=ali", getHeaders());
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.getInt("code") == 205) {
            if (time_205 > 2) {
                SpiderDebug.log("yiso 搜索次数限制");
                return "{}";
            }
            time_205++;
            return searchContent(key, quick);
        }
        JSONArray array = jsonObject.getJSONObject("data").getJSONArray("list");
        ArrayList<Vod> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Vod vod = new Vod();
            String name = array.getJSONObject(i).getJSONArray("fileInfos").getJSONObject(0).getString("fileName");
            String url = decrypt(array.getJSONObject(i).getString("url"));
            String remark = array.getJSONObject(i).getString("gmtCreate");
            vod.setVodId(url);
            vod.setVodName(name);
            vod.setVodRemarks(remark);
//            vod.setVodPlayFrom(super.detailContentVodPlayFrom(Collections.singletonList(url)));
//            vod.setVodPlayUrl(super.detailContentVodPlayUrl(Collections.singletonList(url)));
            vod.setVodPic("https://inews.gtimg.com/newsapp_bt/0/13263837859/1000");
            list.add(vod);
        }
        return Result.string(list);
    }

    public String decrypt(String str) {
        try {
            SecretKeySpec key = new SecretKeySpec("4OToScUFOaeVTrHE".getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec iv = new IvParameterSpec("9CLGao1vHKqm17Oz".getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(str)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
