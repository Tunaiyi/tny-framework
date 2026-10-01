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

import com.tny.game.common.utils.*;

import java.math.*;
import java.util.function.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 16/2/17.
 */
@SuppressWarnings("unused")
public class NumberAide {

    private static final int[] intSizeTable = new int[String.valueOf(Integer.MAX_VALUE).length()];

    private static final long[] longSizeTable = new long[String.valueOf(Long.MAX_VALUE).length()];

    // public static final long[] intSizeTable;

    static {
        int intLimit = 1;
        for (int index = 0; index < intSizeTable.length - 1; index++) {
            intLimit *= 10;
            intSizeTable[index] = intLimit - 1;
        }
        intSizeTable[intSizeTable.length - 1] = Integer.MAX_VALUE;

        long longLimit = 1;
        for (int index = 0; index < longSizeTable.length - 1; index++) {
            longLimit *= 10;
            longSizeTable[index] = longLimit - 1;
        }
        longSizeTable[longSizeTable.length - 1] = Long.MAX_VALUE;
    }

    private static int getDigitsLength(int value) {
        value = value > 0 ? value : -value;
        for (int index = 0; index < intSizeTable.length; index++) {
            if (value <= intSizeTable[index]) {
                return index + 1;
            }
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("all")
    public static long getDigitsLength(long value) {
        value = value > 0 ? value : -value;
        for (int index = 0; index < longSizeTable.length; index++) {
            if (value <= longSizeTable[index]) {
                return index + 1;
            }
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        for (int i : intSizeTable) {
            System.out.println(i + " >> " + getDigitsLength(i));
        }
        for (long i : longSizeTable) {
            System.out.println(i + " >> " + getDigitsLength(i));
        }
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N as(Number source, N target) {
        Number value;
        if (target instanceof Integer) {
            value = source.intValue();
        } else if (target instanceof Long) {
            value = source.longValue();
        } else if (target instanceof Float) {
            value = source.floatValue();
        } else if (target instanceof Double) {
            value = source.doubleValue();
        } else if (target instanceof Short) {
            value = source.shortValue();
        } else if (target instanceof Byte) {
            value = source.byteValue();
        } else if (target instanceof BigDecimal) {
            value = toBigDecimal(source);
        } else if (target instanceof BigInteger) {
            value = toBigInteger(source);
        } else {
            // 原 else 兜底静默折叠 intValue：交回"看似成功实则截断/错型"的实例（BREAKING：显式受控失败）
            throw new IllegalArgumentException(
                    format("不支持的数值目标类型 {}（源值 {}）", target == null ? "null" : target.getClass().getName(), source));
        }
        return (N) value;
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N as(Number source, Class<N> clazz) {
        //        Number value;
        //        if (!(source instanceof Number)) {
        //            throw new ClassCastException(format("{} {} 不属于 {}", source, source.getClass(), Number.class));
        //        }
        //        value = (Number)source;
        if (Integer.class == clazz || int.class == clazz) {
            source = source.intValue();
        } else if (Long.class == clazz || long.class == clazz) {
            source = source.longValue();
        } else if (Float.class == clazz || float.class == clazz) {
            source = source.floatValue();
        } else if (Double.class == clazz || double.class == clazz) {
            source = source.doubleValue();
        } else if (Short.class == clazz || short.class == clazz) {
            source = source.shortValue();
        } else if (Byte.class == clazz || byte.class == clazz) {
            source = source.byteValue();
        } else if (BigDecimal.class == clazz) {
            source = toBigDecimal(source);
        } else if (BigInteger.class == clazz) {
            source = toBigInteger(source);
        } else {
            // 原 else 兜底静默折叠 intValue：交回"看似成功实则截断/错型"的实例（BREAKING：显式受控失败）
            throw new IllegalArgumentException(format("不支持的数值目标类型 {}（源值 {}）", clazz, source));
        }
        return (N) source;
    }

    public static <N extends Number> N parse(String source, Class<N> clazz) {
        if (Integer.class == clazz || int.class == clazz) {
            return ObjectAide.as(Integer.parseInt(source));
        } else if (Long.class == clazz || long.class == clazz) {
            return ObjectAide.as(Long.parseLong(source));
        } else if (Float.class == clazz || float.class == clazz) {
            return ObjectAide.as(Float.parseFloat(source));
        } else if (Double.class == clazz || double.class == clazz) {
            return ObjectAide.as(Double.parseDouble(source));
        } else if (Short.class == clazz || short.class == clazz) {
            return ObjectAide.as(Short.parseShort(source));
        } else if (Byte.class == clazz || byte.class == clazz) {
            return ObjectAide.as(Byte.parseByte(source));
        }
        throw new IllegalArgumentException(
                format("{} is not number", source));
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N add(N one, N other) {
        return operate(one, other, ADD);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N sub(N one, N other) {
        return operate(one, other, SUBTRACT);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N multiply(N one, N other) {
        return operate(one, other, MULTIPLY);
    }

    public static <N extends Number> double divideAsDouble(N one, N other) {
        if (one == null) {
            return other.floatValue();
        }
        if (other == null) {
            return one.floatValue();
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        return as(one.doubleValue() / other.doubleValue(), Double.class);
    }

    public static <N extends Number> float divideAsFloat(N one, N other) {
        if (one == null) {
            return other.floatValue();
        }
        if (other == null) {
            return one.floatValue();
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class) || numClass.isAssignableFrom(Float.class) || numClass.isAssignableFrom(Short.class) ||
            numClass.isAssignableFrom(Byte.class)) {
            return as(one.floatValue() / other.floatValue(), Float.class);
        }
        // if (numClass.isAssignableFrom(Long.class) || numClass.isAssignableFrom(Double.class))
        //     return as(one.doubleValue() / other.doubleValue(), Float.class);
        return as(one.doubleValue() / other.doubleValue(), Float.class);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N divide(N one, N other) {
        return operate(one, other, DIVIDE);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N mod(N one, N other) {
        return operate(one, other, MOD);
    }

    /**
     * add/sub/multiply/divide/mod 五段同形骨架的唯一分派器（reduce-code-duplication D8：私有分派器 + 运算符 lambda）。
     * 方法体逐字搬运自原五段克隆（null 处理/BigDecimal 优先/BigInteger/7 路 isAssignableFrom 基本类型分派/结果
     * as(x, one) 折叠回第一操作数类型），零行为变更；现状可疑语义原样保留、禁止顺手修：
     * <ul>
     * <li>{@code one == null} 返回 other——sub/divide/mod 亦不取反/不抛（NumberAideTest.nullCombinations 钉桩）；</li>
     * <li>混合类型运算通道由 findClass 决定、结果却恒按 {@code one} 窄化折叠（add(Integer,Long) 得 Integer，
     * add(byte 100, short 300) 溢出为 -112）；</li>
     * <li>未知数值形态兜底 double 通道后 as(x, one) 显式受控失败（fix 轮现状）。</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private static <N extends Number> N operate(N one, N other, NumberOperation operation) {
        if (one == null) {
            return other;
        }
        if (other == null) {
            return one;
        }
        if (one instanceof BigDecimal || other instanceof BigDecimal) {
            return (N) operation.bigDecimal.apply(toBigDecimal(one), toBigDecimal(other));
        }
        if (one instanceof BigInteger || other instanceof BigInteger) {
            return (N) operation.bigInteger.apply(toBigInteger(one), toBigInteger(other));
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class)) {
            return as(operation.integer.applyAsInt(one.intValue(), other.intValue()), one);
        }
        if (numClass.isAssignableFrom(Long.class)) {
            return as(operation.longValue.applyAsLong(one.longValue(), other.longValue()), one);
        }
        if (numClass.isAssignableFrom(Float.class)) {
            return as(operation.floatValue.apply(one.floatValue(), other.floatValue()), one);
        }
        if (numClass.isAssignableFrom(Double.class)) {
            return as(operation.doubleValue.applyAsDouble(one.doubleValue(), other.doubleValue()), one);
        }
        if (numClass.isAssignableFrom(Short.class)) {
            return as(operation.integer.applyAsInt(one.shortValue(), other.shortValue()), one);
        }
        if (numClass.isAssignableFrom(Byte.class)) {
            return as(operation.integer.applyAsInt(one.byteValue(), other.byteValue()), one);
        }
        return as(operation.doubleValue.applyAsDouble(one.doubleValue(), other.doubleValue()), one);
    }

    /**
     * 基本类型通道的 int 形态运算（Integer/Short/Byte 三路共用，短/字窄化即原实现 int 提升语义）。
     */
    private static final class NumberOperation {

        private final BinaryOperator<BigDecimal> bigDecimal;

        private final BinaryOperator<BigInteger> bigInteger;

        private final IntBinaryOperator integer;

        private final LongBinaryOperator longValue;

        private final FloatBinaryOperator floatValue;

        private final DoubleBinaryOperator doubleValue;

        private NumberOperation(BinaryOperator<BigDecimal> bigDecimal, BinaryOperator<BigInteger> bigInteger,
                                IntBinaryOperator integer, LongBinaryOperator longValue,
                                FloatBinaryOperator floatValue, DoubleBinaryOperator doubleValue) {
            this.bigDecimal = bigDecimal;
            this.bigInteger = bigInteger;
            this.integer = integer;
            this.longValue = longValue;
            this.floatValue = floatValue;
            this.doubleValue = doubleValue;
        }
    }

    /**
     * java.util.function 无 float 二元运算接口，本表驱动所需。
     */
    @FunctionalInterface
    private interface FloatBinaryOperator {

        float apply(float one, float other);
    }

    private static final NumberOperation ADD = new NumberOperation(BigDecimal::add, BigInteger::add,
            (one, other) -> one + other, (one, other) -> one + other, (one, other) -> one + other,
            (one, other) -> one + other);

    private static final NumberOperation SUBTRACT = new NumberOperation(BigDecimal::subtract, BigInteger::subtract,
            (one, other) -> one - other, (one, other) -> one - other, (one, other) -> one - other,
            (one, other) -> one - other);

    private static final NumberOperation MULTIPLY = new NumberOperation(BigDecimal::multiply, BigInteger::multiply,
            (one, other) -> one * other, (one, other) -> one * other, (one, other) -> one * other,
            (one, other) -> one * other);

    private static final NumberOperation DIVIDE = new NumberOperation(
            // 现状 BigDecimal 除法定标 DECIMAL128（NumberAideTest 钉桩）
            (one, other) -> one.divide(other, MathContext.DECIMAL128), BigInteger::divide,
            (one, other) -> one / other, (one, other) -> one / other, (one, other) -> one / other,
            (one, other) -> one / other);

    private static final NumberOperation MOD = new NumberOperation(
            // remainder 与 Java % 语义一致（截断取余）——BigDecimal/BigInteger/基本类型三路现状同源
            BigDecimal::remainder, BigInteger::remainder,
            (one, other) -> one % other, (one, other) -> one % other, (one, other) -> one % other,
            (one, other) -> one % other);

    public static boolean less(Number one, Number other) {
        if (isHighPrecision(one) || isHighPrecision(other)) {
            return compareExact(one, other) < 0;
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class)) {
            return one.intValue() < other.intValue();
        }
        if (numClass.isAssignableFrom(Long.class)) {
            return one.longValue() < other.longValue();
        }
        if (numClass.isAssignableFrom(Float.class)) {
            return one.floatValue() < other.floatValue();
        }
        if (numClass.isAssignableFrom(Double.class)) {
            return one.doubleValue() < other.doubleValue();
        }
        if (numClass.isAssignableFrom(Short.class)) {
            return one.shortValue() < other.shortValue();
        }
        if (numClass.isAssignableFrom(Byte.class)) {
            return one.byteValue() < other.byteValue();
        }
        return one.doubleValue() < other.doubleValue();
    }

    public static boolean lessEqual(Number one, Number other) {
        if (isHighPrecision(one) || isHighPrecision(other)) {
            return compareExact(one, other) <= 0;
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class)) {
            return one.intValue() <= other.intValue();
        }
        if (numClass.isAssignableFrom(Long.class)) {
            return one.longValue() <= other.longValue();
        }
        if (numClass.isAssignableFrom(Float.class)) {
            return one.floatValue() <= other.floatValue();
        }
        if (numClass.isAssignableFrom(Double.class)) {
            return one.doubleValue() <= other.doubleValue();
        }
        if (numClass.isAssignableFrom(Short.class)) {
            return one.shortValue() <= other.shortValue();
        }
        if (numClass.isAssignableFrom(Byte.class)) {
            return one.byteValue() <= other.byteValue();
        }
        return one.doubleValue() <= other.doubleValue();
    }

    public static boolean greater(Number one, Number other) {
        if (isHighPrecision(one) || isHighPrecision(other)) {
            return compareExact(one, other) > 0;
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class)) {
            return one.intValue() > other.intValue();
        }
        if (numClass.isAssignableFrom(Long.class)) {
            return one.longValue() > other.longValue();
        }
        if (numClass.isAssignableFrom(Float.class)) {
            return one.floatValue() > other.floatValue();
        }
        if (numClass.isAssignableFrom(Double.class)) {
            return one.doubleValue() > other.doubleValue();
        }
        if (numClass.isAssignableFrom(Short.class)) {
            return one.shortValue() > other.shortValue();
        }
        if (numClass.isAssignableFrom(Byte.class)) {
            return one.byteValue() > other.byteValue();
        }
        return one.doubleValue() > other.doubleValue();
    }

    public static boolean greaterEqual(Number one, Number other) {
        if (isHighPrecision(one) || isHighPrecision(other)) {
            return compareExact(one, other) >= 0;
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class)) {
            return one.intValue() >= other.intValue();
        }
        if (numClass.isAssignableFrom(Long.class)) {
            return one.longValue() >= other.longValue();
        }
        if (numClass.isAssignableFrom(Float.class)) {
            return one.floatValue() >= other.floatValue();
        }
        if (numClass.isAssignableFrom(Double.class)) {
            return one.doubleValue() >= other.doubleValue();
        }
        if (numClass.isAssignableFrom(Short.class)) {
            return one.shortValue() >= other.shortValue();
        }
        if (numClass.isAssignableFrom(Byte.class)) {
            return one.byteValue() >= other.byteValue();
        }
        return one.doubleValue() >= other.doubleValue();
    }

    public static Number min(Number one, Number other) {
        return lessEqual(one, other) ? one : other;
    }

    public static Number max(Number one, Number other) {
        return greaterEqual(one, other) ? one : other;
    }

    public static boolean equal(Number one, Number other) {
        if (isHighPrecision(one) || isHighPrecision(other)) {
            return compareExact(one, other) == 0;
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class)) {
            return one.intValue() == other.intValue();
        }
        if (numClass.isAssignableFrom(Long.class)) {
            return one.longValue() == other.longValue();
        }
        if (numClass.isAssignableFrom(Float.class)) {
            return one.floatValue() == other.floatValue();
        }
        if (numClass.isAssignableFrom(Double.class)) {
            return one.doubleValue() == other.doubleValue();
        }
        if (numClass.isAssignableFrom(Short.class)) {
            return one.shortValue() == other.shortValue();
        }
        if (numClass.isAssignableFrom(Byte.class)) {
            return one.byteValue() == other.byteValue();
        }
        return one.doubleValue() == other.doubleValue();
    }

    public static int compare(Number one, Number other) {
        if (isHighPrecision(one) || isHighPrecision(other)) {
            return compareExact(one, other);
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class) || numClass.isAssignableFrom(Short.class) || numClass.isAssignableFrom(Byte.class)) {
            // 原 int 相减在 MAX_VALUE vs MIN_VALUE 等极值下溢出翻号
            return Integer.compare(one.intValue(), other.intValue());
        }
        if (numClass.isAssignableFrom(Long.class)) {
            long value = one.longValue() - other.longValue();
            return value == 0 ? 0 : value > 0 ? 1 : -1;
        }
        if (numClass.isAssignableFrom(Float.class)) {
            float value = one.floatValue() - other.floatValue();
            return value == 0.F ? 0 : value > 0 ? 1 : -1;
        }
        double value = one.doubleValue() - other.doubleValue();
        return value == 0. ? 0 : value > 0 ? 1 : -1;
    }

    public static boolean notEqual(Number one, Number other) {
        return !equal(one, other);
    }

    private static final Class<?>[] NUM_CLASSES = new Class<?>[]{BigDecimal.class, BigInteger.class, Double.class, Float.class, Long.class,
                                                                  Integer.class, Short.class, Byte.class};

    private static Class<?> findClass(Class<?>... classes) {
        for (Class<?> clazz : NUM_CLASSES) {
            for (Class<?> findClass : classes) {
                if (clazz.isAssignableFrom(findClass)) {
                    return findClass;
                }
            }
        }
        // 未知数值形态的兜底仍是 Double（NUM_CLASSES[0] 已让位给高精度类型）
        return Double.class;
    }

    private static boolean isHighPrecision(Number number) {
        return number instanceof BigDecimal || number instanceof BigInteger;
    }

    private static BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }
        if (number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return BigDecimal.valueOf(number.longValue());
    }

    private static BigInteger toBigInteger(Number number) {
        if (number instanceof BigInteger) {
            return (BigInteger) number;
        }
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        }
        if (number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue()).toBigInteger();
        }
        return BigInteger.valueOf(number.longValue());
    }

    /**
     * 高精度参与者按数值真值比较，不得经 doubleValue 窄化折叠制造伪相等/伪不等。
     */
    private static int compareExact(Number one, Number other) {
        if (one instanceof BigDecimal || other instanceof BigDecimal) {
            return toBigDecimal(one).compareTo(toBigDecimal(other));
        }
        return toBigInteger(one).compareTo(toBigInteger(other));
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(byte x, byte y) {
        return Byte.compare(x, y);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(byte x, byte y) {
        return Byte.compare(y, x);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(short x, short y) {
        return Short.compare(x, y);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(short x, short y) {
        return Short.compare(y, x);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(int x, int y) {
        return Integer.compare(x, y);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(int x, int y) {
        return Integer.compare(y, x);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(long x, long y) {
        return Long.compare(x, y);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(long x, long y) {
        return Long.compare(y, x);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(float x, float y) {
        return Float.compare(x, y);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(float x, float y) {
        return Float.compare(y, x);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(double x, double y) {
        return Double.compare(x, y);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(double x, double y) {
        return Double.compare(y, x);
    }

    private static int shift(int length) {
        int shift = 0;
        int value = 0;
        for (int index = 0; index < 8; index++) {
            int current = (value << 1) | 1;
            if (current <= length) {
                shift++;
                value = current;
            } else {
                break;
            }
        }
        return shift;
    }

    // public static String numberUnsignedConvert(long val, ScaleCharacterSet set) {
    //     int mag = Long.SIZE - Long.numberOfLeadingZeros(val);
    //     int shift = shift(set.length());
    //     int chars = Math.max(((mag + (shift - 1)) / shift), 1);
    //     char[] buf = new char[chars];
    //     formatUnsignedInt(val, shift, buf, 0, chars, set);
    //     return new String(buf);
    // }
    //
    // public static String numberUnsignedConvert(int val, ScaleCharacterSet set) {
    //     int mag = Integer.SIZE - Integer.numberOfLeadingZeros(val);
    //     int shift = shift(set.length());
    //     int chars = Math.max(((mag + (shift - 1)) / shift), 1);
    //     char[] buf = new char[chars];
    //     formatUnsignedInt(val, shift, buf, 0, chars, set);
    //     return new String(buf);
    // }
    //
    // private static void formatUnsignedInt(long val, int shift, char[] buf, int offset, int len, ScaleCharacterSet set) {
    //     int charPos = len;
    //     int radix = 1 << shift;
    //     int mask = radix - 1;
    //     do {
    //         buf[offset + --charPos] = set.getChar((int)(val & mask));
    //         val >>>= shift;
    //     } while (val != 0 && charPos > 0);
    // }
    //
    // private static void formatUnsignedInt(int val, int shift, char[] buf, int offset, int len, ScaleCharacterSet set) {
    //     int charPos = len;
    //     int radix = 1 << shift;
    //     int mask = radix - 1;
    //     do {
    //         buf[offset + --charPos] = set.getChar(val & mask);
    //         val >>>= shift;
    //     } while (val != 0 && charPos > 0);
    // }

    public static String numberConverter(int number, ScaleCharacterSet set) {
        int radix = set.length();
        if (radix < Character.MIN_RADIX) {
            radix = 10;
        }
        if (radix == 10) {
            return Long.toString(number);
        }
        char[] buf = new char[33];
        int charPos = buf.length - 1;
        int value = number;
        boolean negative = (value < 0);
        if (!negative) {
            value = -value;
        }
        while (value <= -radix) {
            buf[charPos--] = set.getChar(-(value % radix));
            value = value / radix;
        }
        buf[charPos] = set.getChar(-value);
        if (negative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (buf.length - charPos));
    }

    public static String numberConverter(long number, ScaleCharacterSet set) {
        int radix = set.length();
        if (radix < Character.MIN_RADIX) {
            radix = 10;
        }
        if (radix == 10) {
            return Long.toString(number);
        }
        char[] buf = new char[65];
        int charPos = buf.length - 1;
        long value = number;
        boolean negative = (value < 0);
        if (!negative) {
            value = -value;
        }
        while (value <= -radix) {
            buf[charPos--] = set.getChar((int) (-(value % radix)));
            value = value / radix;
        }
        buf[charPos] = set.getChar((int) (-value));
        if (negative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (buf.length - charPos));
    }

}
