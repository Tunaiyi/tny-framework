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
package com.tny.game.common.concurrent.collection;

import com.tny.game.common.collection.map.FixLinkedHashMap;
import com.tny.game.common.collection.map.MapBuilder;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * concurrent-collection-contracts 契约（fix-common-dormant-defects 组4）：
 * 写时复制映射的读视图=获取时刻不可变快照且严格只读；有界映射容量精确；
 * 映射后备集与底表双向同步、迭代移除生效；不可变条目按内容判等；构建器产物双向隔离。
 */
class ConcurrentCollectionContractsTest {

    // ---- CopyOnWriteMap 视图语义 ----

    @Test
    void viewIsSnapshotAtAcquisition() {
        CopyOnWriteMap<String, Integer> map = new CopyOnWriteMap<>();
        map.put("a", 1);
        Set<String> snapshot = map.keySet();
        map.put("b", 2);
        map.remove("a");
        // 获取时刻快照：后续写不影响已取视图，旧键仍在、新键不出现
        assertEquals(Set.of("a"), new HashSet<>(snapshot), "视图未冻结在获取时刻");
    }

    @Test
    void viewsAreStrictlyReadOnly() {
        CopyOnWriteMap<String, Integer> map = new CopyOnWriteMap<>();
        map.put("k", 1);
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().add("x"));
        assertThrows(UnsupportedOperationException.class, () -> map.keySet().remove("k"),
                "视图修改必须显式拒绝（原实现改写被丢弃的旧代、静默丢效果）");
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().clear());
        assertThrows(UnsupportedOperationException.class, () -> map.values().clear());
        // 主接口写不受影响
        map.remove("k");
        assertTrue(map.isEmpty());
    }

    @Test
    void entrySnapshotCarriesAcquisitionContent() {
        CopyOnWriteMap<String, Integer> map = new CopyOnWriteMap<>();
        map.put("x", 1);
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        map.put("x", 99);
        Map.Entry<String, Integer> only = entries.iterator().next();
        assertEquals(1, only.getValue(), "entry 视图未快照获取时刻的值");
    }

    // ---- 有界映射容量精确（规格2）----

    @Test
    void fixLinkedMapHoldsExactlyMaxSize() {
        FixLinkedHashMap<Integer, Integer> map = new FixLinkedHashMap<>(3);
        for (int i = 0; i < 3; i++) {
            map.put(i, i);
        }
        assertEquals(3, map.size(), "满容必须保留 maxSize 条（原差一只容 2）");
        map.put(3, 3);
        assertEquals(3, map.size());
        assertFalse(map.containsKey(0), "最旧应被驱逐");
        assertTrue(map.containsKey(3));
    }

    @Test
    void maxSizeOneHoldsOneEntry() {
        FixLinkedHashMap<Integer, Integer> map = new FixLinkedHashMap<>(1);
        map.put(1, 1);
        assertEquals(1, map.size(), "容量 1 的映射放入后不得自我清空");
        map.put(2, 2);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(2));
    }

    @Test
    void nonPositiveMaxSizeConstructionFailsExplicitly() {
        // 钉桩：规格『非正上限构造失败（错误路径）』——以 0 或负数构造必须显式失败并给出非法参数信号，
        // 不产出 put 即自驱逐的行为未定义实例（钉桩时构造器无校验，此断言跑红）
        IllegalArgumentException zero =
                assertThrows(IllegalArgumentException.class, () -> new FixLinkedHashMap<>(0),
                        "以 0 为上限构造必须显式失败");
        assertTrue(zero.getMessage() != null && zero.getMessage().contains("0"),
                "错误信号须含非法参数值，实际: " + zero.getMessage());
        IllegalArgumentException negative =
                assertThrows(IllegalArgumentException.class, () -> new FixLinkedHashMap<>(-3),
                        "以负数为上限构造必须显式失败");
        assertTrue(negative.getMessage() != null && negative.getMessage().contains("-3"),
                "错误信号须含非法参数值，实际: " + negative.getMessage());
    }

    // ---- 映射后备集（规格3）----

    @Test
    void mapBackedSetSyncsWithBackingMap() {
        Map<String, Boolean> backing = new HashMap<>();
        MapBackedSet<String> set = new MapBackedSet<>(backing);
        assertTrue(set.add("a"));
        assertFalse(set.add("a"));
        assertTrue(backing.containsKey("a"), "集合元素未落底表");
        backing.remove("a");
        assertFalse(set.contains("a"), "底表变更未反映到集合");
        set.add("b");
        set.add("c");
        Iterator<String> it = set.iterator();
        String first = it.next();
        it.remove();
        assertFalse(backing.containsKey(first), "迭代移除必须落到底表");
        assertEquals(1, set.size());
    }

    // ---- 不可变条目（规格4）----

    @Test
    void immutableEntryContentEqualityAndWriteRejection() {
        Map.Entry<String, Integer> one = ImmutableEntry.entry("k", 7);
        Map.Entry<String, Integer> same = ImmutableEntry.entry("k", 7);
        assertEquals(one, same, "内容相同的条目必须判等（原按引用）");
        assertEquals(one.hashCode(), same.hashCode());
        assertThrows(UnsupportedOperationException.class, () -> one.setValue(8));
        // null 侧正常参与判等/哈希（契约：如实记录，不 NPE）
        Map.Entry<String, Integer> nullKey = ImmutableEntry.entry(null, 1);
        Map.Entry<String, Integer> nullKeySame = ImmutableEntry.entry(null, 1);
        assertEquals(nullKey, nullKeySame);
        assertDoesNotThrow(nullKey::hashCode);
        assertNotEquals(nullKey, ImmutableEntry.entry(null, 2));
    }

    // ---- 构建器双向隔离（规格5）----

    @Test
    void builderResultIsIsolatedFromSourceAndBuilder() {
        Map<String, Integer> source = new HashMap<>();
        source.put("a", 1);
        MapBuilder<String, Integer> builder = MapBuilder.newBuilder(source);
        builder.put("b", 2);
        Map<String, Integer> built = builder.build();
        built.put("c", 3);
        // 三向隔离：源不感知 builder/产物；builder 不感知产物；产物独立可变
        assertEquals(Set.of("a"), source.keySet(), "源映射被构建器/产物穿透");
        assertEquals(Set.of("a", "b", "c"), new HashSet<>(built.keySet()));
    }

}
