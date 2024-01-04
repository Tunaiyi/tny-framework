/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
public class EnumKeyDeserializer<T> extends KeyDeserializer {

    private final EnumMapper<T> enumMapper;

    public EnumKeyDeserializer(EnumMapper<T> enumMapper) {
        this.enumMapper = enumMapper;
    }

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        Object enumObject = this.enumMapper.getEnum(key);
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", key));
        }
        return enumObject;
    }

}
