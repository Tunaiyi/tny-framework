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
package com.tny.game.common.io.config;

import org.junit.jupiter.api.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChildConfig 委托契约（回归：单参 getLong/getDouble/getFloat/getBoolean/getByte/getObject/getEnum(Class)
 * 原为常量桩；嵌套 child 的 subKey 剥离原用 String.replace 会抹掉中段同名串）。
 */
class ChildConfigTest {

    enum Level {
        LOW, MIDDLE, HIGH
    }

    private static Config baseConfig() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("db.timeout", "5000");
        data.put("db.long_key", "9000000000000");
        data.put("db.double_key", "1.25");
        data.put("db.float_key", "0.5");
        data.put("db.enable", "true");
        data.put("db.level", "MIDDLE");
        data.put("db.int_key", "42");
        data.put("db.byte_key", "7");
        data.put("db.l1.l1.num_key.value", "11");
        data.put("other.key", "99");
        return ConfigLib.newConfig(data);
    }

    /** 单参方法必须等价于双参方法（无默认值版本走各自缺省），不得返回常量桩 */
    @Test
    void singleArgGettersDelegateToParent() {
        Config db = baseConfig().child("db");
        assertEquals(9000000000000L, db.getLong("long_key"));
        assertEquals(5000L, db.getLong("timeout"));
        assertEquals(1.25d, db.getDouble("double_key"), 0d);
        assertEquals(0.5f, db.getFloat("float_key"), 0f);
        assertTrue(db.getBoolean("enable"));
        assertEquals((byte) 7, db.getByte("byte_key"));
        assertEquals(42, db.getInt("int_key"));
        assertEquals("5000", db.getObject("timeout"));
        assertEquals(Level.MIDDLE, db.getEnum("level", Level.class));
    }

    /** 缺键时单参方法返回各自类型的缺省值（而非桩常量的巧合值） */
    @Test
    void missingKeysUseDefaults() {
        Config db = baseConfig().child("db");
        assertEquals(0L, db.getLong("absent"));
        assertEquals(-1L, db.getLong("absent", -1L));
        assertEquals(0d, db.getDouble("absent"), 0d);
        assertEquals(0f, db.getFloat("absent"), 0f);
        assertFalse(db.getBoolean("absent"));
        assertTrue(db.getBoolean("absent", true));
        assertEquals((byte) 0, db.getByte("absent"));
        assertNull(db.getObject("absent"));
        assertEquals(Level.HIGH, db.getEnum("absent", Level.HIGH));
    }

    /**
     * 嵌套 child：中段与头串同名（"l1." 在 "l1.l1.num_key" 中出现两次）时只剥一次前缀，
     * 不得用 String.replace 抹掉全部出现处（原实现会解析到不存在的键恒 null）。
     */
    @Test
    void nestedChildStripsPrefixOnly() {
        Config root = baseConfig();
        Config db = root.child("db");
        Config l1 = db.child("db.l1"); // l1 的 head = "l1."（相对 db 空间）
        // 传入 key "l1.l1.num_key" 含两个 "l1."：正确剥离应保留嵌套路径
        Config l2 = l1.child("l1.l1.num_key");
        assertEquals("11", l2.getString("value"), "嵌套 child 前缀剥离错误，解析到了错误键");
        // child key 必须以当前 head 为前缀
        assertThrows(IllegalArgumentException.class, () -> l1.child("other.key"));
    }

    /** parentKey/parentHeadKey 语义（现有有效断言的保护性回归） */
    @Test
    void parentKeyAccessors() {
        Config db = baseConfig().child("db");
        assertEquals("db", db.parentKey());
        assertEquals("db.", db.parentHeadKey());
        assertEquals("db.l1", db.child("db.l1").parentKey());
    }

    /** 空 parentKey 现在会被拒绝（原断言误用 checkNotNull(boolean) 永不生效） */
    @Test
    void blankParentKeyRejected() {
        Config root = baseConfig();
        assertThrows(IllegalArgumentException.class, () -> root.child(""));
        assertThrows(IllegalArgumentException.class, () -> root.child(null));
    }

    /** entrySet/keySet 返回绝对键（现契约），过滤仅覆盖本前缀下的项 */
    @Test
    void entrySetScopedByPrefixAbsoluteKeys() {
        Config db = baseConfig().child("db");
        assertEquals(9, db.keySet().size());
        assertTrue(db.keySet().contains("db.timeout"));
        assertFalse(db.keySet().contains("other.key"));
    }

}
