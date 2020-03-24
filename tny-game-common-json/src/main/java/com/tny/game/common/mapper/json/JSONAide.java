package com.tny.game.common.mapper.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.*;

import java.io.*;

/**
 * 包含操作 {@code JSON} 数据的常用方法的工具类。
 *
 * @author KGTny
 * @version 1.0, 2009-6-27
 */
public final class JSONAide {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONAide.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object value) {
        return toJson(mapper, value);
    }

    public static String toJson(ObjectMapper mapper, Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            LOGGER.error("转换json出错", e);
            throw new RuntimeException("转换json出错", e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return toObject(mapper, json, clazz);
    }

    public static <T> T toObject(ObjectMapper mapper, String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("json转换对象出错", e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> type) {
        return toObject(mapper, json, type);
    }

    public static <T> T toObject(ObjectMapper mapper, String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            LOGGER.error("json转换对象 {} 出错", json, e);
            throw new RuntimeException("转换json出错", e);
        }
    }

    public static <T> T toObject(InputStream inputStream, TypeReference<T> type) {
        return toObject(mapper, inputStream, type);
    }

    public static <T> T toObject(ObjectMapper mapper, InputStream inputStream, TypeReference<T> type) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (IOException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("转换json出错", e);
        }
    }
}