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
package com.tny.game.net.netty4.apm.skywalking;

import org.apache.skywalking.apm.agent.core.context.tag.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 26（Wave-C）红灯基线：SkyWalking tag 字典 ID 唯一（同 ID 不同 key 会在 agent 侧互相顶掉）。
 * 修复前 102 被 CONTACT/TARGET/FORWARD 共用、108 被 SPAN_ID/START_TIME/END_TIME 共用。
 */
class ApmTagUniquenessTest {

    private static int tagId(AbstractTag<?> tag) throws Exception {
        for (Class<?> c = tag.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field idField = c.getDeclaredField("id");
                idField.setAccessible(true);
                return idField.getInt(tag);
            } catch (NoSuchFieldException ignored) {
                // 继续向上查找
            }
        }
        throw new IllegalStateException("tag 无 id 字段: " + tag.getClass());
    }

    @Test
    @DisplayName("所有 StringTag 常量 ID 互不相同")
    void stringTagIdsAreUnique() throws Exception {
        Map<Integer, String> byId = new HashMap<>();
        for (Field field : SkywalkingRpcMonitorHandler.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !AbstractTag.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            AbstractTag<?> tag = (AbstractTag<?>) field.get(null);
            int id = tagId(tag);
            String previous = byId.put(id, field.getName());
            assertNull(previous, "tag id " + id + " 冲突: " + previous + " vs " + field.getName());
        }
        assertFalse(byId.isEmpty(), "反射未采集到任何 tag 常量（测试本身失效）");
    }

}
