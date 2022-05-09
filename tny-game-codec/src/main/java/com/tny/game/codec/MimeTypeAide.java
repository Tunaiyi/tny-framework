package com.tny.game.codec;

import com.tny.game.codec.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MimeType;

import java.util.*;

/**
 * @author Song Tao
 * @date 2020/08/03 11:55
 */
public interface MimeTypeAide {

    String NONE = "";
    String WILDCARD = "*";
    String TYPE = "application";

    static String wildcardType(String subType) {
        return TYPE + "/" + WILDCARD + "+" + subType;
    }

    static List<MimeType> asList(String... types) {
        List<MimeType> typeList = new ArrayList<>();
        for (String type : types) {
            typeList.add(MimeType.valueOf(type));
        }
        return typeList;
    }

    static String getMimeType(Class<?> entityClass) {
        return getMimeType(entityClass, null);
    }

    static String getMimeType(Class<?> entityClass, String defaultType) {
        Codable codecable = entityClass.getAnnotation(Codable.class);
        String mimeType = null;
        if (codecable != null) {
            mimeType = codecable.value();
        }
        return StringUtils.isBlank(mimeType) ? defaultType : mimeType;
    }

    static String getMimeType(Codable codecable) {
        if (StringUtils.isNotBlank(codecable.mimeType())) {
            return codecable.mimeType();
        }
        if (StringUtils.isNotBlank(codecable.value())) {
            return codecable.value();
        }
        return MimeTypeAide.NONE;
    }

}
