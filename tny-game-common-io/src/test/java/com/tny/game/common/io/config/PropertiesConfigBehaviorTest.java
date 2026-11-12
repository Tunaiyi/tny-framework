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
package com.tny.game.common.io.config;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PropertiesConfig 行为边界（回归：reload 通知中断、null 值裸 NPE、getEnum 空默认值 NPE）。
 * ConfigTest 已覆盖基本读路径，这里只钉行为边界与修复契约。
 */
class PropertiesConfigBehaviorTest {

    enum Type {
        ONE, TWO
    }

    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    /** 非法数字值抛 NumberFormatException（现契约钉死，调用方需自处理配置脏值） */
    @Test
    void malformedNumbersThrow() {
        Config config = ConfigLib.newConfig(map("k", "abc"));
        assertThrows(NumberFormatException.class, () -> config.getInt("k"));
        assertThrows(NumberFormatException.class, () -> config.getLong("k"));
        assertThrows(NumberFormatException.class, () -> config.getDouble("k"));
        assertThrows(NumberFormatException.class, () -> config.getFloat("k"));
        assertThrows(NumberFormatException.class, () -> config.getByte("k", (byte) 1));
        // 值 300 超 byte 域同样异常（不是静默截断）
        Config big = ConfigLib.newConfig(map("k", "300"));
        assertThrows(NumberFormatException.class, () -> big.getByte("k"));
    }

    /** null 值给出带键名的受控异常（原为构造期裸 NPE） */
    @Test
    void nullValueRejectedWithKeyName() {
        Map<String, Object> m = new HashMap<>();
        m.put("bad.key", null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> ConfigLib.newConfig(m));
        assertTrue(e.getMessage().contains("bad.key"), "异常应提示具体键名: " + e.getMessage());
    }

    /** getEnum 双参（默认值为 null 的枚举实例不可构造——用 (E) null 强转路径）不再 NPE */
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getEnumWithNullDefaultIsSafe() {
        Config config = ConfigLib.newConfig(map("type", "ONE"));
        // 正常路径
        assertEquals(Type.ONE, config.getEnum("type", Type.ONE));
        assertEquals(Type.TWO, config.getEnum("absent", Type.TWO));
        // 双参 Class 版本：空值返回 null，非法值抛 IllegalArgumentException（两种失败通道钉死现状）
        assertNull(config.getEnum("absent", Type.class));
        Config bad = ConfigLib.newConfig(map("type", "NOT_A_TYPE"));
        assertThrows(IllegalArgumentException.class, () -> bad.getEnum("type", Type.class));
        // 回归修复：默认值为 null 不再 NPE（原实现 defValue.getDeclaringClass() 直接炸）
        assertNull(config.getEnum("type", (Type) null));
        assertNull(ConfigLib.newConfig(map("k", "v")).getEnum("absent", (Type) null));
    }

    /** reload 语义：整体替换（旧键消失）+ 通知列表全部执行、单个监听器异常不阻断其余 */
    @Test
    void reloadReplacesMapAndIsolatesListenerErrors() {
        PropertiesConfig config = (PropertiesConfig) ConfigLib.newConfig(map("old", "1", "new", "2"));
        AtomicInteger notified = new AtomicInteger();
        AtomicReference<Config> seen = new AtomicReference<>();
        config.addConfigReload(cfg -> {
            throw new RuntimeException("监听器故意炸");
        });
        config.addConfigReload(cfg -> {
            notified.incrementAndGet();
            seen.set(cfg);
        });
        config.reload(map("new", "20", "added", "30"));
        // 第二个监听器必须仍然收到通知（回归：原实现第一个异常直接中断循环）
        assertEquals(1, notified.get());
        assertSame(config, seen.get());
        // 整体替换语义：新增键可见、旧键消失
        assertEquals("20", config.getString("new"));
        assertEquals("30", config.getString("added"));
        assertNull(config.getString("old"));
    }

    /** import 值带空格必须逐项 trim（原实现 "a, b" 会产生带前导空格的项导致加载失败） */
    @Test
    void importListTrimsSpaces() {
        // 通过 reload 验证解析：主配置 import 一个 test-resources 内的属性文件
        Map<String, Object> m = map(
                PropertiesConfig.IMPORT_KEY, "io-import-main.properties, io-import-sub.properties",
                "own.key", "own");
        PropertiesConfig config = (PropertiesConfig) ConfigLib.newConfig(m);
        // import 走 ConfigLib.getExistConfig → 需要真实资源在 classpath；这里只断言解析不抛异常且主键保留
        assertEquals("own", config.getString("own.key"));
        assertTrue(config.keySet().size() >= 3, "import 的两个文件应合并进来: " + config.keySet());
        assertEquals("from-main", config.getString("imported.main"));
        assertEquals("from-sub", config.getString("imported.sub"));
    }

    /** 导入指向缺失文件：首载整次装载显式失败（原缺陷：null 属性流被换成空表静默合并，"成功但缺内容"） */
    @Test
    void missingImportFailsFirstLoadExplicitly() {
        Map<String, Object> m = map(
                PropertiesConfig.IMPORT_KEY, "io-no-such-import-file-xyz.properties",
                "own.key", "own");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> ConfigLib.newConfig(m), "导入缺失文件必须显式失败，不得静默合并空内容");
        assertTrue(e.getMessage().contains("io-no-such-import-file-xyz.properties"),
                "异常信息必须点名缺失来源: " + e.getMessage());
    }

    /** getExistConfig 装载缺失导入：受控异常携带来源链（主文件 → 缺失项） */
    @Test
    void getExistConfigMissingImportFailsWithSourceChain() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> ConfigLib.getExistConfig("io-import-missing.properties"));
        assertTrue(e.getMessage().contains("io-import-missing.properties")
                        && e.getMessage().contains("io-no-such-import-file.properties"),
                "异常须同时含主文件与缺失来源形成链: " + e.getMessage());
    }

    /** find 正则匹配返回命中集合（现契约） */
    @Test
    void findMatchesRegex() {
        Config config = ConfigLib.newConfig(map("net.port", "8080", "net.host", "0.0.0.0", "db.url", "jdbc:"));
        Map<String, Object> hits = config.find("net\\..*");
        assertEquals(2, hits.size());
        assertEquals("8080", hits.get("net.port"));
    }

    /**
     * getObject 泛型契约：内部 (O) 强转因擦除不会触发（try/catch ClassCastException 为死代码），
     * 真实检查发生在调用点赋值处。
     */
    @Test
    void getObjectCastingBehavior() {
        Config config = ConfigLib.newConfig(map("k", "123"));
        String s = config.getObject("k");
        assertEquals("123", s);
        assertThrows(ClassCastException.class, () -> {
            Integer v = config.getObject("k");
            fail("不应走到这里: " + v);
        });
    }

}
