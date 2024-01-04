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

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.common.result.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 3:22 下午
 */
public class ExtensionModule extends SimpleModule {

    public ExtensionModule() {
        extensionModule(Instant.class, InstantJsonSerializer.getDefault(), InstantJsonDeserializer.getDefault());
        extensionModule(ResultCode.class, ResultCodeJsonSerializer.getDefault(), ResultCodeJsonDeserializer.getDefault());
    }

    public <T> void extensionModule(Class<T> clazz, JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
        this.addSerializer(clazz, serializer);
        this.addDeserializer(clazz, deserializer);
    }

}
