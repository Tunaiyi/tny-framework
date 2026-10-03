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

package com.tny.game.common.number;

import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * numeric-hash-integrity 补做契约钉桩（verify 裁决 REAL_GAP 第 1/2/3 项）：
 * 字符集真实长度与超 127 基数换算、LocalNum 窄参数原宽运算、NumberAide 高精度分派与 as() 显式失败。
 */
public class NumericHashIntegrityContractTest {

    // ==================== 1. ScaleCharacterSets / numberConverter ====================

    private static String distinctCharacters(int count) {
        char[] chars = new char[count];
        for (int index = 0; index < count; index++) {
            chars[index] = (char) (0x21 + index);
        }
        return new String(chars);
    }

    /**
     * 换算参考实现：与修复后的实现同构（负值累加避免 MIN_VALUE 溢出），用于钉住 62/128/256 基数的自洽往返。
     */
    private static String referenceConvert(long value, String digits) {
        long radix = digits.length();
        boolean negative = value < 0;
        long working = negative ? value : -value;
        StringBuilder builder = new StringBuilder();
        while (working <= -radix) {
            builder.append(digits.charAt((int) -(working % radix)));
            working = working / radix;
        }
        builder.append(digits.charAt((int) -working));
        if (negative) {
            builder.append('-');
        }
        return builder.reverse().toString();
    }

    private static long referenceParse(String encoded, String digits) {
        int radix = digits.length();
        boolean negative = encoded.charAt(0) == '-';
        long value = 0;
        for (int index = negative ? 1 : 0; index < encoded.length(); index++) {
            int digit = digits.indexOf(encoded.charAt(index));
            assertTrue(digit >= 0, "换算输出的每一位必须取自字符集");
            value = value * radix + digit;
        }
        return negative ? -value : value;
    }

    @Test
    public void charsetLengthIsTrueLengthNotByteTruncated() {
        assertEquals(16, ScaleCharacterSets.HEX_LOWER.length());
        assertEquals(62, ScaleCharacterSets.SIXTY_TWO_SCALE.length());
        ScaleCharacterSet set128 = ScaleCharacterSets.of(distinctCharacters(128));
        assertEquals(128, set128.length(), "超过 127 的字符集长度不得被单字节域回绕");
        ScaleCharacterSet set256 = ScaleCharacterSets.of(distinctCharacters(256));
        assertEquals(256, set256.length(), "256 字符集长度必须报告真实基数 256（原被截断为 0）");
    }

    @Test
    public void charsetRejectsDuplicateCharacters() {
        assertThrows(IllegalArgumentException.class, () -> ScaleCharacterSets.of("aab"));
        assertThrows(IllegalArgumentException.class, () -> ScaleCharacterSets.of("01234567890"));
    }

    @Test
    public void charsetEmptyAndSingleRemainConstructible() {
        // 构造空集拒绝属规格越界（规格已收窄）：空集/单字符集按既有语义可构造，长度如实为 0/1
        ScaleCharacterSet empty = ScaleCharacterSets.of("");
        assertEquals(0, empty.length());
        ScaleCharacterSet single = ScaleCharacterSets.of("a");
        assertEquals(1, single.length());
    }

    @Test
    public void conversionSixtyTwoBackwardCompatible() {
        String digits = ScaleCharacterSets.SIXTY_TWO_SCALE.getKey();
        Random random = new Random(20261001L);
        long[] samples = new long[256];
        for (int index = 0; index < samples.length; index++) {
            samples[index] = random.nextLong();
        }
        for (long value : samples) {
            assertEquals(referenceConvert(value, digits), NumberAide.numberConverter(value, ScaleCharacterSets.SIXTY_TWO_SCALE));
            assertEquals(value, referenceParse(NumberAide.numberConverter(value, ScaleCharacterSets.SIXTY_TWO_SCALE), digits));
        }
        // int 入口同样钉参考
        for (int index = 0; index < 64; index++) {
            int value = random.nextInt();
            String digits62 = ScaleCharacterSets.SIXTY_TWO_SCALE.getKey();
            assertEquals(referenceConvert(value, digits62), NumberAide.numberConverter(value, ScaleCharacterSets.SIXTY_TWO_SCALE));
        }
    }

    @Test
    public void conversion128UsesTrueRadix() {
        String digits = distinctCharacters(128);
        ScaleCharacterSet set = ScaleCharacterSets.of(digits);
        long[] samples = {0, 1, 127, 128, 127 * 128 + 127, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, 123456789012345L};
        for (long value : samples) {
            String encoded = NumberAide.numberConverter(value, set);
            assertEquals(referenceConvert(value, digits), encoded);
            assertEquals(value, referenceParse(encoded, digits), "按 128 进制自洽往返");
            assertNotEquals(Long.toString(value), encoded, "不得退化为普通十进制文本假成功");
        }
    }

    @Test
    public void conversion256UsesTrueRadixRoundTrip() {
        String digits = distinctCharacters(256);
        ScaleCharacterSet set = ScaleCharacterSets.of(digits);
        Random random = new Random(42L);
        long[] samples = {0, 1, 255, 256, 255 * 256 + 255, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, 999999999999999L};
        for (long value : samples) {
            String encoded = NumberAide.numberConverter(value, set);
            assertEquals(referenceConvert(value, digits), encoded);
            assertEquals(value, referenceParse(encoded, digits), "256 字符集按真实基数 256 往返");
            assertNotEquals(Long.toString(value), encoded);
        }
        for (int index = 0; index < 64; index++) {
            long value = random.nextLong();
            String encoded = NumberAide.numberConverter(value, set);
            assertEquals(value, referenceParse(encoded, digits));
            String intEncoded = NumberAide.numberConverter((int) (value >> 32), set);
            assertEquals((int) (value >> 32), referenceParse(intEncoded, digits));
        }
    }

    // ==================== 2. LocalNum 窄参数原宽运算 ====================

    @Test
    public void longStoreShortParamAddNoTruncation() {
        LocalNum<Long> holder = new LocalNum<>(500000L);
        assertEquals(500001L, holder.add((short) 1).longValue());
        assertEquals(500001L, holder.getNumber().longValue());
        assertInstanceOf(Long.class, holder.getNumber(), "结果按存储值类型写回");
        // 与六十四位参数入口完全一致
        LocalNum<Long> referenceHolder = new LocalNum<>(500000L);
        assertEquals(referenceHolder.add(1L).longValue(), new LocalNum<>(500000L).add((short) 1).longValue());
    }

    @Test
    public void longStoreNarrowParamAllFiveOps() {
        assertEquals(499999L, new LocalNum<>(500000L).sub((short) 1).longValue());
        assertEquals(1000000L, new LocalNum<>(500000L).multiply((short) 2).longValue());
        assertEquals(250000L, new LocalNum<>(500000L).divide((short) 2).longValue());
        assertEquals(500000L % 7L, new LocalNum<>(500000L).mod((short) 7).longValue());
        assertEquals(500001L, new LocalNum<>(500000L).add((byte) 1).longValue());
        assertEquals(499999L, new LocalNum<>(500000L).sub((byte) 1).longValue());
        assertEquals(1000000L, new LocalNum<>(500000L).multiply((byte) 2).longValue());
        assertEquals(250000L, new LocalNum<>(500000L).divide((byte) 2).longValue());
        assertEquals(500000L % 7L, new LocalNum<>(500000L).mod((byte) 7).longValue());
    }

    @Test
    public void entryWidthEquivalence() {
        assertEquals(new LocalNum<>(100).add(5), new LocalNum<>(100).add(5L));
        assertEquals(new LocalNum<>(100).add(5), new LocalNum<>(100).add((short) 5));
        assertEquals(new LocalNum<>(100).add(5), new LocalNum<>(100).add((byte) 5));
        assertEquals(Integer.valueOf(105), new LocalNum<>(100).add((short) 5));
        assertEquals(Integer.valueOf(96), new LocalNum<>(100).sub((byte) 4));
        assertEquals(Integer.valueOf(200), new LocalNum<>(100).multiply((short) 2));
        assertEquals(Integer.valueOf(25), new LocalNum<>(100).divide((byte) 4));
        assertEquals(Integer.valueOf(2), new LocalNum<>(100).mod((short) 7));
        assertEquals(Integer.valueOf(25), new LocalNum<>(100).divide((short) 4));
        assertEquals(Integer.valueOf(2), new LocalNum<>(100).mod((byte) 7));
    }

    @Test
    public void shortStoreKeepsShortTypeAfterNarrowAdd() {
        LocalNum<Short> holder = new LocalNum<>((short) 100);
        assertEquals(Short.valueOf((short) 101), holder.add((short) 1));
        assertInstanceOf(Short.class, holder.getNumber(), "结果类型不越界假宽");
        LocalNum<Byte> byteHolder = new LocalNum<>((byte) 100);
        assertEquals(Byte.valueOf((byte) 101), byteHolder.add((short) 1));
        assertInstanceOf(Byte.class, byteHolder.getNumber());
    }

    // ==================== 3. NumberAide 高精度分派与 as() 显式失败 ====================

    @Test
    public void bigDecimalOneVsTenDispatch() {
        BigDecimal sum = NumberAide.add(BigDecimal.ONE, BigDecimal.TEN);
        assertInstanceOf(BigDecimal.class, sum);
        assertEquals(0, sum.compareTo(new BigDecimal("11")));
        BigDecimal difference = NumberAide.sub(BigDecimal.TEN, BigDecimal.ONE);
        assertEquals(0, difference.compareTo(new BigDecimal("9")));
        BigDecimal product = NumberAide.multiply(BigDecimal.ONE, BigDecimal.TEN);
        assertEquals(0, product.compareTo(new BigDecimal("10")));
        BigDecimal quotient = NumberAide.divide(new BigDecimal("10"), new BigDecimal("4"));
        assertEquals(0, quotient.compareTo(new BigDecimal("2.5")));
        BigDecimal remainder = NumberAide.mod(new BigDecimal("10"), new BigDecimal("3"));
        assertEquals(0, remainder.compareTo(BigDecimal.ONE));
        assertTrue(NumberAide.less(BigDecimal.ONE, BigDecimal.TEN));
        assertFalse(NumberAide.equal(BigDecimal.ONE, BigDecimal.TEN));
        assertEquals(-1, NumberAide.compare(BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    public void bigDecimalKeepsFractionPart() {
        BigDecimal sum = NumberAide.add(new BigDecimal("1.5"), new BigDecimal("2.25"));
        assertInstanceOf(BigDecimal.class, sum);
        assertEquals(0, sum.compareTo(new BigDecimal("3.75")), "小数部分不得被折叠成整数");
    }

    @Test
    public void bigIntegerBeyondLongRangeExact() {
        BigInteger a = new BigInteger("12345678901234567890123456789012345678901234567890");
        BigInteger b = new BigInteger("98765432109876543210987654321098765432109876543210");
        BigInteger sum = NumberAide.add(a, b);
        assertInstanceOf(BigInteger.class, sum);
        assertEquals(new BigInteger("111111111011111111101111111110111111111011111111100"), sum, "四十位相加逐位精确，无高位丢失");
        BigInteger product = NumberAide.multiply(a, b);
        assertEquals(a.multiply(b), product);
        BigInteger difference = NumberAide.sub(sum, b);
        assertEquals(a, difference);
        BigInteger quotient = NumberAide.divide(b, a);
        assertEquals(b.divide(a), quotient);
        BigInteger remainder = NumberAide.mod(b, a);
        assertEquals(b.remainder(a), remainder);
    }

    @Test
    public void bigIntegerComparisonMatchesTrueValue() {
        BigInteger a = BigInteger.TEN.pow(40);
        BigInteger b = a.add(BigInteger.ONE);
        assertEquals(a.doubleValue(), b.doubleValue(), "先证明 double 域无法区分（伪相等陷阱存在）");
        assertTrue(NumberAide.less(a, b), "比较必须与数值真值一致，不得因窄化折叠伪相等");
        assertTrue(NumberAide.lessEqual(a, b));
        assertFalse(NumberAide.greater(a, b));
        assertFalse(NumberAide.equal(a, b));
        assertTrue(NumberAide.notEqual(a, b));
        assertEquals(-1, NumberAide.compare(a, b));
        assertEquals(1, NumberAide.compare(b, a));
        assertSame(a, NumberAide.min(a, b));
        assertSame(b, NumberAide.max(a, b));
    }

    @Test
    public void mixedHighPrecisionPromotesNotTruncates() {
        Number sum = NumberAide.<Number>add(BigDecimal.ONE, Integer.valueOf(5));
        assertInstanceOf(BigDecimal.class, sum);
        assertEquals(0, ((BigDecimal) sum).compareTo(new BigDecimal("6")));
    }

    @Test
    public void asFoldsNoUnknownTargetType() {
        // 未知目标类型：显式受控失败（原静默折叠 intValue）
        assertThrows(IllegalArgumentException.class, () -> NumberAide.as(5L, AtomicInteger.class));
        assertThrows(IllegalArgumentException.class, () -> NumberAide.as(Long.valueOf(5L), (Number) new AtomicInteger(0)));
    }

    @Test
    public void asSupportsHighPrecisionTargetsAndKnownTargetsUnchanged() {
        Number widened = NumberAide.as(5L, BigDecimal.class);
        assertInstanceOf(BigDecimal.class, widened, "BigDecimal 已纳入承载类型");
        assertEquals(0, new BigDecimal("5").compareTo((BigDecimal) widened));
        assertEquals(Integer.valueOf(9), NumberAide.as(9L, Integer.class));
        assertEquals(Long.valueOf(9), NumberAide.as(9, Long.class));
        assertEquals(Short.valueOf((short) 9), NumberAide.as(9.75, Short.class));
        assertEquals(Byte.valueOf((byte) 9), NumberAide.as(9L, Byte.class));
        assertEquals(Float.valueOf(9f), NumberAide.as(9L, Float.class));
        assertEquals(Double.valueOf(9d), NumberAide.as(9L, Double.class));
    }

    @Test
    public void divideAsDoubleAndNullsUnchanged() {
        assertEquals(2.5d, NumberAide.divideAsDouble(new BigDecimal("10"), new BigDecimal("4")), 1e-9);
        assertNull(NumberAide.add(null, null));
        assertEquals(Integer.valueOf(3), NumberAide.add(1, 2));
    }

}
