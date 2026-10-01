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

package com.tny.game.common.number;

import java.math.*;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class NumberAideTest {

    // ==================================================================================
    // reduce-code-duplication D8 钉桩（task 4.3）：add/sub/multiply/divide/mod 五运算符现状语义。
    // 重构前绿、五段收敛为"私有分派器 + 运算符 lambda"后期望值一字不改仍须绿。
    // 含现状可疑语义（sub 的 one==null 返回 other、混合类型结果恒按 one 窄化折叠等）——禁止顺手修。
    // ==================================================================================

    // ==================================================================================
    // fix-registered-defects D1 翻转（task 4.1）：五算子 null 操作数=零元语义（P4：运算通道内 null 无独立
    // 身份，视作缺席操作数即零）。原钉桩"五方法 one==null 一律返回 other"整体翻到规格承诺方向：
    // sub 空被减数取零后取反（BREAKING：此前返回 other 原值）；divide/mod 除数侧落零（null 视零后、及显式零）
    // =受控失败（BREAKING：此前返回 other、浮点通道返 Infinity/NaN 伪值）；add 兼容格（结果=另一操作数原值）逐字保留。
    // ==================================================================================

    @Test
    void nullCombinationsSharedAcrossOperators() {
        Integer five = 5;
        // add 零元兼容格（矩阵证据：LocalNum 委托面依赖 add(null,5)=5）——逐字保留
        assertSame(five, NumberAide.add(null, five));
        assertSame(five, NumberAide.add(five, null));
        // sub：空被减数取零后取反（BREAKING：此前直接返回 5）；空减数 5−0=5（兼容格保留）
        assertEquals(-5, NumberAide.sub(null, five).intValue(), "空被减数=0−5，取反对侧值");
        assertSame(five, NumberAide.sub(five, null));
        // multiply：空操作数=乘的零元 → 0（BREAKING：此前返回 5）
        assertEquals(0, NumberAide.multiply(null, five).intValue());
        assertEquals(0, NumberAide.multiply(five, null).intValue());
        // divide：空被除数（视零）÷5=0；空除数（视零后）→ 当场受控失败（此前返回 5）
        assertEquals(0, NumberAide.divide(null, five).intValue());
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(five, null));
        // mod：与 divide 同形
        assertEquals(0, NumberAide.mod(null, five).intValue());
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(five, null));
        // 双空：add/sub/multiply 无落型通道维持返 null；divide/mod 除数侧空值零化后仍须显式失败
        assertNull(NumberAide.add(null, null));
        assertNull(NumberAide.sub(null, null));
        assertNull(NumberAide.multiply(null, null));
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(null, null));
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(null, null));
        // 零除数矩阵（显式零，全通道）：受控失败方向对齐既有高精度路径，不得返 Infinity/NaN 伪值
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(7, 0));
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(7, 0));
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(7L, 0L));
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(7L, 0L));
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(1.0f, 0.0f));
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(1.0f, 0.0f));
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(1.0d, 0.0d));
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(1.0d, 0.0d));
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(BigDecimal.ONE, BigDecimal.ZERO),
                   "BigDecimal 除零：DECIMAL128 受控失败（既有格方向保持）");
        assertThrows(ArithmeticException.class, () -> NumberAide.mod(BigDecimal.ONE, BigDecimal.ZERO),
                   "BigDecimal remainder 零除数同形失败");
    }

    @Test
    void integerBranchesAndFirstOperandDominantResultType() {
        Number r = NumberAide.<Integer>add(5, 3);
        assertEquals(8, r.intValue());
        assertInstanceOf(Integer.class, r);
        // 现状：结果恒经 as(x, one) 折叠回 one 的类型——混合 Integer/Long 时运算走 long 通道、结果仍是 Integer
        Number mixed = NumberAide.<Number>add(5, 3L);
        assertInstanceOf(Integer.class, mixed);
        assertEquals(8, mixed.intValue());
        assertEquals(8, NumberAide.sub(11, 3).intValue());
        assertEquals(15, NumberAide.multiply(3, 5).intValue());
        assertEquals(3, NumberAide.divide(7, 2).intValue(), "现状 int 通道整除截断");
        assertEquals(-3, NumberAide.divide(-7, 2).intValue(), "整除向零截断（非 floorDiv）");
        assertEquals(1, NumberAide.mod(7, -2).intValue(), "Java % 截断取余语义");
        assertEquals(-1, NumberAide.mod(-7, 2).intValue());
    }

    @Test
    void integerDivisionByZeroThrowsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> NumberAide.divide(1, 0));
    }

    @Test
    void longFloatDoubleBranches() {
        Number longResult = NumberAide.<Number>add(9_000_000_000L, 1L);
        assertInstanceOf(Long.class, longResult);
        assertEquals(9_000_000_001L, longResult.longValue());
        assertEquals(3L, NumberAide.divide(7L, 2L).longValue());

        assertEquals(3.0f, NumberAide.<Number>divide(7.5f, 2.5f).floatValue(), 0f);
        assertInstanceOf(Float.class, NumberAide.divide(7.5f, 2.5f));

        // 现状 double 通道为二进制浮点：0.1+0.2 不折叠为十进制 0.3
        double d = NumberAide.<Number>add(0.1d, 0.2d).doubleValue();
        assertEquals(0.30000000000000004d, d, 0d);

        assertEquals(20d, NumberAide.<Number>multiply(2.5d, 8d).doubleValue(), 0d);
        assertEquals(1.5d, NumberAide.<Number>mod(7.5d, 3d).doubleValue(), 0d);
    }

    // ==================================================================================
    // fix-registered-defects D2 翻转（task 4.3）：窄类型提升照 JLS 二元数值提升（P6 与平台语义对齐）。
    // 原钉桩"运算按 int 进行、结果恒 as(one) 折叠回首操作数原宽"翻到承诺方向：byte/short 参与二元运算
    // 先提升至 int 通道，落型按提升后宽度（BREAKING：add((byte)100,(short)300) 由 -112/Byte → 400/Integer；
    // short×short 同宽亦 Integer，不再按首操作数回折）；含 long 参与者走 long 通道、落型 Long（BREAKING：
    // 此前 add((byte)100, 300L) 折叠回 byte 溢出 -112）。既有纯值断言格（不依赖落型）逐字保留。
    // ==================================================================================

    @Test
    void shortByteBranchesNarrowToFirstOperand() {
        Number shortResult = NumberAide.<Number>multiply((short) 3, (short) 4);
        assertInstanceOf(Integer.class, shortResult, "short×short 提升 int 通道（BREAKING：此前折叠回 Short）");
        assertEquals(12, shortResult.intValue());
        Number byteOne = NumberAide.<Number>add((byte) 100, (short) 300);
        assertInstanceOf(Integer.class, byteOne, "窄×窄混合提升 int 通道，不按首操作数回折（BREAKING）");
        assertEquals(400, byteOne.intValue(), "原溢出折叠 -112 → 数值真值 400");
        // 新增同宽超界格：byte+byte 超字节值域、short+short 超短整值域，均提升通道表示且真值正确
        Number byteSum = NumberAide.<Number>add((byte) 100, (byte) 100);
        assertInstanceOf(Integer.class, byteSum);
        assertEquals(200, byteSum.intValue(), "此前回折 byte 溢出 -56");
        Number shortSum = NumberAide.<Number>add((short) 32000, (short) 32000);
        assertInstanceOf(Integer.class, shortSum);
        assertEquals(64000, shortSum.intValue(), "此前回折 short 溢出 -1536");
        // 新增 long 混合格：含长整参与者按既有优先级通道提升，落型进 long 通道
        Number longMix = NumberAide.<Number>add((byte) 100, 300L);
        assertInstanceOf(Long.class, longMix, "窄×long 混合走 long 通道（BREAKING：此前折叠回 byte 溢出 -112）");
        assertEquals(400L, longMix.longValue());
        Number longMix2 = NumberAide.<Number>add(1000L, (short) 300);
        assertInstanceOf(Long.class, longMix2);
        assertEquals(1300L, longMix2.longValue());
        // 既有纯值断言格保留（值不变；<Number> 见证补位——D2 后落型为 Integer，
        // 原按推断 N=Byte 的调用点会 checkcast 失败，属 BREAKING 公告的调用面影响）
        assertEquals(3, NumberAide.<Number>add((byte) 1, 2).intValue());
        assertEquals((byte) 1, NumberAide.<Number>mod((byte) 7, (byte) 2).byteValue());
    }

    @Test
    void bigDecimalHighPrecisionPaths() {
        Number sum = NumberAide.add(new BigDecimal("0.1"), 2);
        assertInstanceOf(BigDecimal.class, sum);
        assertEquals(new BigDecimal("2.1"), sum);

        // 现状 BigDecimal 优先于 BigInteger 混合：BigInteger×BigDecimal 走十进制通道
        Number mixed = NumberAide.<Number>add(BigInteger.ONE, new BigDecimal("2.5"));
        assertInstanceOf(BigDecimal.class, mixed);
        assertEquals(new BigDecimal("3.5"), mixed);

        // 除法定标 DECIMAL128（现状唯一使用点）：1/3 取 34 位精度
        Number third = NumberAide.divide(BigDecimal.ONE, BigDecimal.valueOf(3));
        assertEquals(BigDecimal.ONE.divide(BigDecimal.valueOf(3), MathContext.DECIMAL128), third);

        // 现状 mod 走 remainder（截断取余，非 floor）
        assertEquals(new BigDecimal("1.5"), NumberAide.mod(new BigDecimal("10.5"), new BigDecimal("3")));
        assertEquals(new BigDecimal("-1.5"), NumberAide.mod(new BigDecimal("-10.5"), new BigDecimal("3")));

        assertThrows(ArithmeticException.class,
                     () -> NumberAide.divide(BigDecimal.ONE, BigDecimal.ZERO),
                     "BigDecimal 除零：DECIMAL128 仍抛 ArithmeticException（现状）");
    }

    @Test
    void bigIntegerHighPrecisionPaths() {
        assertEquals(BigInteger.valueOf(7), NumberAide.add(BigInteger.valueOf(3), 4L));
        Number quotient = NumberAide.divide(BigInteger.valueOf(7), 2);
        assertInstanceOf(BigInteger.class, quotient);
        assertEquals(BigInteger.valueOf(3), quotient, "BigInteger.divide 向零截断（现状）");
        assertEquals(BigInteger.valueOf(-3), NumberAide.divide(BigInteger.valueOf(-7), BigInteger.valueOf(2)));
        assertEquals(BigInteger.valueOf(1), NumberAide.mod(BigInteger.valueOf(7), BigInteger.valueOf(2)));
        assertEquals(BigInteger.valueOf(-1), NumberAide.mod(BigInteger.valueOf(-7), BigInteger.valueOf(2)),
                   "BigInteger.remainder 截断语义（非 floorMod）");
        // Double 参与 BigInteger 运算：toBigInteger 先截断小数
        assertEquals(BigInteger.valueOf(6), NumberAide.<Number>add(BigInteger.valueOf(5), 1.9d));
    }

    @Test
    void unknownNumberTypeFallsToDoubleChannelThenExplicitFailure() {
        // 现状：findClass 兜底 Double → double 通道计算后 as(x, one=AtomicLong) 无匹配目标类型 → 显式受控失败
        // （fix-common-dormant-defects 已把静默 intValue 折叠改为抛 IllegalArgumentException，本变更原样保留）
        assertThrows(IllegalArgumentException.class,
                     () -> NumberAide.add(new AtomicLong(5), new AtomicLong(7)));
        assertThrows(IllegalArgumentException.class,
                     () -> NumberAide.multiply(new AtomicLong(5), new AtomicLong(7)));
    }

    @Test
    public void testConvertString() {
        // assertEquals(Integer.toString(1), NumberAide.numberConverter(-1, ScaleCharacterSets.HEX_LOWER));
        assertEquals(Integer.toString(Integer.MIN_VALUE, 16), NumberAide.numberConverter(Integer.MIN_VALUE, ScaleCharacterSets.HEX_LOWER));
        for (int index = 0; index < 500; index++) {
            assertEquals(Integer.toString(index, 16), NumberAide.numberConverter(index, ScaleCharacterSets.HEX_LOWER));
        }
        assertEquals(Integer.toString(Integer.MAX_VALUE, 16), NumberAide.numberConverter(Integer.MAX_VALUE, ScaleCharacterSets.HEX_LOWER));
        ScaleCharacterSet set = ScaleCharacterSets.SIXTY_TWO_SCALE;
        for (int index = 0; index <= set.length(); index++) {
            System.out.println(NumberAide.numberConverter(index, set));
        }
    }

}