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
package com.tny.game.common.math;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * weighted-random-selection 契约（fix-common-dormant-defects 组6）：
 * 每轮恰抽一值且条数精确、默认仅真实落空兜底、文档分布必须兑现（累积语义）、
 * 参数错误显式异常、权重读取与分桶聚合不再断链。
 * 统计断言以固定样本量+区间界执行（区间按 ±大容差设计，确定性偏差远大于容差），无 sleep。
 */
class WeightedSelectionContractTest {

    private static List<RandomObject<String>> cumulativeItems() {
        // 累积权重语义：a:0-2 b:3-4 c:5-9（总 10）
        return List.of(new RandomObject<>("a", 3), new RandomObject<>("b", 5), new RandomObject<>("c", 10));
    }

    // ---- 多轮抽取：每轮恰一、条数精确 ----

    @Test
    void randObjectsReturnsExactlyTimesItems() {
        List<RandomObject<String>> items = cumulativeItems();
        for (int times : new int[]{1, 10, 1000}) {
            List<String> drawn = MathAide.randObjects(10, times, items);
            assertEquals(times, drawn.size(),
                    "抽取 " + times + " 次返回 " + drawn.size() + " 项（一次命中叠加多值）");
        }
    }

    /** 默认值仅在该轮真实落空时出现：number 大于累积上界的窗口占比可观测 */
    @Test
    void defaultOnlyOnRealMiss() {
        List<RandomObject<String>> items = cumulativeItems();
        int number = 20; // 10-19 为真实落空区（累积上界 10）
        int times = 30000;
        List<String> drawn = MathAide.randObjects(number, times, items, "DEF");
        long defCount = drawn.stream().filter("DEF"::equals).count();
        long knownCount = drawn.stream().filter(v -> v.equals("a") || v.equals("b") || v.equals("c")).count();
        assertEquals(times, drawn.size());
        // 落空区 10/20=50%（±10 容差）
        assertTrue(defCount > times * 0.4 && defCount < times * 0.6,
                "默认值占比异常（应≈50% 落空区）: " + defCount);
        assertEquals(times - defCount, knownCount, "存在未知值或统计不闭合");
    }

    /** 命中区间按累积权重：a=30% b=20% c=50% */
    @Test
    void cumulativeDistributionHonored() {
        List<RandomObject<String>> items = cumulativeItems();
        Map<String, Integer> hits = new HashMap<>();
        int times = 60000;
        for (String v : MathAide.randObjects(10, times, items)) {
            hits.merge(v, 1, Integer::sum);
        }
        assertClose(0.30, hits.getOrDefault("a", 0) / (double) times, 0.03, "a");
        assertClose(0.20, hits.getOrDefault("b", 0) / (double) times, 0.03, "b");
        assertClose(0.50, hits.getOrDefault("c", 0) / (double) times, 0.03, "c");
    }

    // ---- 文档成对参数版：[100:"a",200:"b"] 各占 100 区间 ----

    /** 按 javadoc：a:0-99 b:100-199 其余默认（原端点构造本对，降序排序致高界垄断、a 永不可中） */
    @Test
    void pairedListVersionHonorsDocumentedRanges() {
        List<Object> pairs = List.of(100, "a", 200, "b");
        int number = 300;
        int times = 60000;
        Map<String, Integer> hits = new HashMap<>();
        for (int i = 0; i < times; i++) {
            String v = MathAide.rand(number, pairs, "DEF");
            hits.merge(v, 1, Integer::sum);
        }
        assertClose(1 / 3d, hits.getOrDefault("a", 0) / (double) times, 0.03, "a(0-99)");
        assertClose(1 / 3d, hits.getOrDefault("b", 0) / (double) times, 0.03, "b(100-199)");
        assertClose(1 / 3d, hits.getOrDefault("DEF", 0) / (double) times, 0.03, "def(200-299)");
    }

    @Test
    void pairedListOddLengthFailsExplicitly() {
        List<Object> broken = List.of(100, "a", 200);
        assertThrows(IllegalArgumentException.class, () -> MathAide.rand(300, broken, "DEF"),
                "成对参数奇数长度必须显式参数异常（原数组越界泄漏）");
    }

    // ---- lot 抽签 ----

    @Test
    void lotDistributionAndGuards() {
        List<Object> pairs = List.of(1, "a", 2, "b", 3, "c");
        Map<String, Integer> hits = new HashMap<>();
        int times = 60000;
        for (int i = 0; i < times; i++) {
            hits.merge(MathAide.lot(pairs), 1, Integer::sum);
        }
        assertClose(1 / 6d, hits.getOrDefault("a", 0) / (double) times, 0.03, "lot a");
        assertClose(2 / 6d, hits.getOrDefault("b", 0) / (double) times, 0.03, "lot b");
        assertClose(3 / 6d, hits.getOrDefault("c", 0) / (double) times, 0.03, "lot c");
        assertThrows(IllegalArgumentException.class, () -> MathAide.lot(List.of(1, "a", 2)),
                "奇数参数显式异常（原越界）");
        assertThrows(IllegalArgumentException.class, () -> MathAide.lot(List.of(0, "a", 0, "b")),
                "权重和非正显式异常");
    }

    // ---- 限次概率抽取 ----

    /** 越过所有阈值键时取"最大档概率值"（原取次数键当概率，概率缩 10 倍） */
    @Test
    void limitedProbabilityUsesValueBeyondThresholdKeys() {
        Map<Integer, Integer> probs = new TreeMap<>(Map.of(3, 30));
        int samples = 40000;
        int appeared = 0;
        for (int i = 0; i < samples; i++) {
            // moreTime=5 ≥ 唯一键 3 → 概率取档位值 30/10000=0.3%
            if (MathAide.randLimited(10, 1, probs, 0, 5, 0)) {
                appeared++;
            }
        }
        // 30/10000*40000=120；原实现 lastKey=3 → ≈12 次。下界 60 稳定区分对错。
        assertTrue(appeared > 60 && appeared < 200,
                "超阈后概率取值错误（出现 " + appeared + "/" + samples + "，期望≈120）");
    }

    @Test
    void limitedProbabilityEmptyTableFailsExplicitly() {
        assertThrows(IllegalArgumentException.class,
                () -> MathAide.randLimited(10, 1, new TreeMap<>(), 0, 5, 0),
                "空概率表显式参数异常（原 NoSuchElementException 泄漏）");
    }

    // ---- Weight 断链与分桶聚合 ----

    @Test
    void weightReadsDeclaredValue() {
        WeightNum<String> num = new Weight<>("A", "30").getWeight();
        assertEquals(30, num.getWeight(), "权重读取恒 0 断链（原公式注释后未给数值通道）");
        assertEquals("A", num.getValue());
    }

    @Test
    void counterBucketsCumulativeProportions() {
        List<Weight<String>> weights = List.of(
                new Weight<>("A", "30"), new Weight<>("B", "30"), new Weight<>("C", "40"));
        SortedMap<Integer, String> proMap = new WeightCounter<>(100, weights).parseProMap();
        Map<Integer, String> expected = new HashMap<>();
        expected.put(30, "A");
        expected.put(60, "B");
        expected.put(100, "C");
        assertEquals(expected, new HashMap<>(proMap), "分桶聚合错误（原全挤 key=0 互相覆盖）");
    }

    @Test
    void counterRejectsNonPositiveTotalWeight() {
        List<Weight<String>> zeroWeights = List.of(new Weight<>("A", "0"), new Weight<>("B", "0"));
        assertThrows(IllegalArgumentException.class,
                () -> new WeightCounter<>(100, zeroWeights).parseProMap());
    }

    private static void assertClose(double expected, double actual, double tolerance, String label) {
        assertTrue(Math.abs(expected - actual) < tolerance,
                label + " 占比 " + actual + " 偏离期望 " + expected + " 超容差 " + tolerance);
    }

}
