package com.github.catvod.utils;

import com.github.catvod.crawler.SpiderDebug;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

public class Json {
    private static Gson gson = new Gson();

    public static Gson get(){
        return gson;
    }

    public static JsonElement parse(String json) {
        try {
            return JsonParser.parseString(json);
        } catch (Throwable e) {
            return new JsonParser().parse(json);
        }
    }

    public static <T> T parseSafe(String json, Type t) {
        try {
            return gson.fromJson(json, t);
        } catch (JsonSyntaxException e) {
            SpiderDebug.log("json parse error: " + e.getMessage() + "\n" + " " + json);
            return null;
        }
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
