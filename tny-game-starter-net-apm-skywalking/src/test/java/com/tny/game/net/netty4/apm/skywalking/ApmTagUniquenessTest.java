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
