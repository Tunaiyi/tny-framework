package com.tny.game.common.utils.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 包含操作 {@code JSON} 数据的常用方法的工具类。
 *
 * @author KGTny
 * @version 1.0, 2009-6-27
 */
public final class JSONUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonGenerationException e) {
            LOGGER.error("转换json出错", e);
            throw new RuntimeException("转换json出错", e);
        } catch (JsonMappingException e) {
            LOGGER.error("转换json出错", e);
            throw new RuntimeException("转换json出错", e);
        } catch (IOException e) {
            LOGGER.error("转换json出错", e);
            throw new RuntimeException("转换json出错", e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("json转换对象出错", e);
        } catch (JsonMappingException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("json转换对象出错", e);
        } catch (IOException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("json转换对象出错", e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonParseException e) {
            LOGGER.error("json转换对象 {} 出错", json, e);
            throw new RuntimeException("转换json出错", e);
        } catch (JsonMappingException e) {
            LOGGER.error("json转换对象 {} 出错", json, e);
            throw new RuntimeException("转换json出错", e);
        } catch (IOException e) {
            LOGGER.error("json转换对象 {} 出错", json, e);
            throw new RuntimeException("转换json出错", e);
        }
    }

    public static <T> T toObject(InputStream inputStream, TypeReference<T> type) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (JsonParseException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("转换json出错", e);
        } catch (JsonMappingException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("转换json出错", e);
        } catch (IOException e) {
            LOGGER.error("json转换对象出错", e);
            throw new RuntimeException("转换json出错", e);
        }
    }
}