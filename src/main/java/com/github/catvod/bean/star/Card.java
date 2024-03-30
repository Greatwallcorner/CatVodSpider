package com.github.catvod.bean.star;

import com.github.catvod.bean.Vod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Card {

    @SerializedName("name")
    private String name;
    @SerializedName(value = "img", alternate = "picurl")
    private String img;
    @SerializedName("id")
    private String id;
    @SerializedName("countStr")
    private String countStr;
    @SerializedName("url")
    private String url;
    @SerializedName("cards")
    private List<Card> cards;

    public static List<Card> arrayFrom(String str) {
        Type listType = new TypeToken<List<Card>>() {
        }.getType();
        return new Gson().fromJson(str, listType);
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "" : name;
    }

    public String getImg() {
        return StringUtils.isEmpty(img) ? "" : img;
    }

    public String getId() {
        return StringUtils.isEmpty(id) ? "" : id;
    }

    public String getCountStr() {
        return StringUtils.isEmpty(countStr) ? "" : countStr;
    }

    public String getUrl() {
        return StringUtils.isEmpty(url) ? "" : url;
    }

    public List<Card> getCards() {
        return cards == null ? Collections.emptyList() : cards;
    }

    public Vod vod() {
        return new Vod(getId(), getName(), getImg(), getCountStr());
    }
}
