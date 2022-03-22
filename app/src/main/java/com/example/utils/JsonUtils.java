package com.example.utils;

import android.net.Uri;
import android.util.Log;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {
    private static final String TAG = "JsonUtil";

    public static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz == Uri.class;
                }
            })
            .disableHtmlEscaping()
            .create();


    /**
     * @param object 即将转换成json字串的对象
     * @return
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /**
     * @param json  格式字串
     * @param clazz json对应的数据模型类对象
     * @param <T>   数据模型模版
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            //do sth.
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }

        return null;
    }


    /**
     * @param json 格式字串
     * @param type json对应的数据模型类对象
     * @param <T>  数据模型模版
     * @return
     */
    public static <T> T fromJson(String json, Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            //do sth.
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param <T>  转换成的集合中数据模型模版
     * @param json 数组格式字串
     * @return
     */
    public static <T> List<T> fromJsonArr(String json, Class clazz) {
        try {
            Type type = new ParameterizedTypeImpl(clazz);

            return GSON.fromJson(json, type);
        } catch (Exception e) {
            //do sth.
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }
        return null;
    }


    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
