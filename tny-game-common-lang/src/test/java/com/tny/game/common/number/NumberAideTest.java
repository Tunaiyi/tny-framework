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

    @Test
    void nullCombinationsSharedAcrossOperators() {
        Integer five = 5;
        // 现状：五方法 null 处理完全一致——one==null 返回 other（注意 sub：不是取反！可疑现状禁止顺手修）
        assertSame(five, NumberAide.add(null, five));
        assertSame(five, NumberAide.sub(null, five));
        assertSame(five, NumberAide.multiply(null, five));
        assertSame(five, NumberAide.divide(null, five));
        assertSame(five, NumberAide.mod(null, five));
        assertSame(five, NumberAide.add(five, null));
        assertSame(five, NumberAide.sub(five, null));
        assertSame(five, NumberAide.multiply(five, null));
        assertSame(five, NumberAide.divide(five, null));
        assertSame(five, NumberAide.mod(five, null));
        assertNull(NumberAide.add(null, null));
        assertNull(NumberAide.sub(null, null));
        assertNull(NumberAide.mod(null, null));
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

    @Test
    void shortByteBranchesNarrowToFirstOperand() {
        Number shortResult = NumberAide.<Short>multiply((short) 3, (short) 4);
        assertInstanceOf(Short.class, shortResult);
        assertEquals(12, shortResult.intValue());
        // 现状短路段：运算按 int 进行、结果 as(one) 窄化——100+300 折叠回 byte 溢出为 -112（可疑现状禁止顺手修）
        Number byteOne = NumberAide.<Number>add((byte) 100, (short) 300);
        assertInstanceOf(Byte.class, byteOne);
        assertEquals(-112, byteOne.byteValue());
        assertEquals(3, NumberAide.<Number>add((byte) 1, 2).intValue());
        assertEquals((byte) 1, NumberAide.mod((byte) 7, (byte) 2).byteValue());
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