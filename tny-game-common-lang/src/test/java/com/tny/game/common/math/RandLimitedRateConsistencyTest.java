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
 * 万分比限步随机器双版本一致契约（weighted-random-selection 二轮 REAL_GAP 3）：
 * randLimited(int prob) 版与 Map 版必须同分母——[0,9999] 均匀（nextInt(10000)），
 * 命中概率恰为 prob/10000。int 版旧实现用 rand(0,10000) 含上界（万分率失真为 prob/10001，
 * 且 prob=10000 时并非恒掉落）。测试钉：prob=10000 恒掉落 + prob=5000 频率落带宽内
 * + 双版本同参频率一致（大数定律容差）。
 */
class RandLimitedRateConsistencyTest {

    /** 强制走随机分支（certainly==0）的参数：time=100,num=1,currentTime=0,currentNum=0 */
    private static final int TIME = 100;

    private static final int NUM = 1;

    private static final int EXTRA = 0;

    private static final int CURRENT_TIME = 0;

    private static final int CURRENT_NUM = 0;

    private static int dropsInt(int prob, int trials) {
        int hits = 0;
        for (int i = 0; i < trials; i++) {
            if (MathAide.randLimited(TIME, NUM, prob, EXTRA, CURRENT_TIME, CURRENT_NUM)) {
                hits++;
            }
        }
        return hits;
    }

    private static int dropsMap(int prob, int trials) {
        // 档位键 1：moreTime(0%100=0) < 1 → 取概率值 prob，与 int 版同分支
        NavigableMap<Integer, Integer> probs = new TreeMap<>();
        probs.put(1, prob);
        int hits = 0;
        for (int i = 0; i < trials; i++) {
            if (MathAide.randLimited(TIME, NUM, probs, EXTRA, CURRENT_TIME, CURRENT_NUM)) {
                hits++;
            }
        }
        return hits;
    }

    /** prob=10000 必须恒掉落（含上界分母下每 10001 次漏 1 次——大样本必现） */
    @Test
    void intVersionMustAlwaysDropAtFullRate() {
        int trials = 400_000;
        int hits = dropsInt(10_000, trials);
        assertEquals(trials, hits,
                "randLimited(int) 含上界分母：prob=10000 存在不掉落抽样（万分率失真）");
    }

    /** Map 版同界恒掉落（双版本钉一致的基准侧） */
    @Test
    void mapVersionMustAlwaysDropAtFullRate() {
        int trials = 400_000;
        int hits = dropsMap(10_000, trials);
        assertEquals(trials, hits, "Map 版 prob=10000 必须恒掉落");
    }

    /** prob=0 必须恒不掉落 */
    @Test
    void zeroProbNeverDrops() {
        assertEquals(0, dropsInt(0, 100_000));
        assertEquals(0, dropsMap(0, 100_000));
    }

    /** 双版本一致：同参 prob=5000 大数频率均落 0.5±1%，且差值远小于单版本自身容差 */
    @Test
    void bothVersionsShareTenThousandthsDenominator() {
        int trials = 200_000;
        double rateInt = dropsInt(5_000, trials) / (double) trials;
        double rateMap = dropsMap(5_000, trials) / (double) trials;
        assertEquals(0.5d, rateInt, 0.01d, "int 版万分率失真：命中率偏离 prob/10000");
        assertEquals(0.5d, rateMap, 0.01d, "Map 版命中率应恰为 prob/10000");
        assertEquals(rateInt, rateMap, 0.015d, "双版本分母语义必须一致");
    }

}
