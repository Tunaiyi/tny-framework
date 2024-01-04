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

package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 2:39 下午
 */
public class InstantJsonDeserializer extends JsonDeserializer<Instant> {

    private static final InstantJsonDeserializer INSTANT = new InstantJsonDeserializer();

    public static InstantJsonDeserializer getDefault() {
        return INSTANT;
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
        long milli = p.getValueAsLong();
        return Instant.ofEpochMilli(milli);
    }

}