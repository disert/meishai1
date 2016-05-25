package com.meishai.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    //private static Gson gson = new GsonBuilder().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
    private static Gson gson = new GsonBuilder().create();

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            if (StringUtils.isNotBlank(json)) {
                return gson.fromJson(json, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseObject(String json, Type type) {
        try {
            if (StringUtils.isNotBlank(json)) {
                return gson.fromJson(json, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object obj) {
        if (obj == null)
            return "";
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
