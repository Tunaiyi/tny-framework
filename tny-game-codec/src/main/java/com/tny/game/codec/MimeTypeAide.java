/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
