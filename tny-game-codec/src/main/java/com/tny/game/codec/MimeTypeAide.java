/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    static String getMimeType(Codable codable) {
        if (StringUtils.isNotBlank(codable.mimeType())) {
            return codable.mimeType();
        }
        if (StringUtils.isNotBlank(codable.value())) {
            return codable.value();
        }
        return MimeTypeAide.NONE;
    }

}
