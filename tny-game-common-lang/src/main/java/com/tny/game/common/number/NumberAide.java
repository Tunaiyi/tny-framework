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
     * fix-registered-defects D1 翻转 null 操作数语义——运算通道内 null 无独立身份，一律视作该运算的数值零元
     * （numeric-hash-integrity 差量"空操作数按零元语义统一参与算术分派"）：
     * <ul>
     * <li>被减数为空：取零后继续（0−other，即另一操作数的相反数；BREAKING，此前直接返回另一操作数原值）；</li>
     * <li>加法空操作数外显结果与差量前一致（0+x=x），既有兼容格逐字保留；</li>
     * <li>除/模的除数侧为空（零化后）或显式零：当场受控失败，不得返回 Infinity/NaN 或静默原值（D1 除零显式失败，
     * 浮点通道见 {@link NumberOperation#explicitZeroDivisor} 守门，int/long 通道 JVM 同源抛出，
     * BigDecimal/BigInteger 既有高精度路径本已受控失败）；</li>
     * <li>双空：divide/mod 除数空值零化仍零 → 显式失败；add/sub/multiply 无落型通道，维持返回 null（现状兼容）。</li>
     * </ul>
     * D2 落型（fix-registered-defects）：findClass 已把 byte/short 统一归并进 int 通道，结果落型按提升后宽度——
     * 首操作数为窄类型（Byte/Short）时不再 as(x, one) 按原宽回折（BREAKING：add((byte)100,(short)300)
     * 由 -112/Byte → 400/Integer，同宽超界亦 Integer，混 long 走 Long 通道）；非窄首操作数的折叠面零改动
     * （add(Integer,Long) 现状 Integer 落型由既有绿格钉住，不在本差量承诺面）。
     */
    @SuppressWarnings("unchecked")
    private static <N extends Number> N operate(N one, N other, NumberOperation operation) {
        if (one == null && other == null) {
            if (operation.explicitZeroDivisor) {
                // 双空时除数侧零化后仍为零元：与单侧空除数同方向受控失败
                throw new ArithmeticException("/ by zero");
            }
            return null;
        }
        if (one == null) {
            one = (N) zeroLike(other);
        } else if (other == null) {
            other = (N) zeroLike(one);
        }
        if (one instanceof BigDecimal || other instanceof BigDecimal) {
            return (N) operation.bigDecimal.apply(toBigDecimal(one), toBigDecimal(other));
        }
        if (one instanceof BigInteger || other instanceof BigInteger) {
            return (N) operation.bigInteger.apply(toBigInteger(one), toBigInteger(other));
        }
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        boolean narrowFirstOperand = Byte.class == one.getClass() || Short.class == one.getClass();
        if (numClass.isAssignableFrom(Integer.class)) {
            int value = operation.integer.applyAsInt(one.intValue(), other.intValue());
            return narrowFirstOperand ? (N) Integer.valueOf(value) : as(value, one);
        }
        if (numClass.isAssignableFrom(Long.class)) {
            long value = operation.longValue.applyAsLong(one.longValue(), other.longValue());
            return narrowFirstOperand ? (N) Long.valueOf(value) : as(value, one);
        }
        if (numClass.isAssignableFrom(Float.class)) {
            // D1 除零显式失败：IEEE 浮点通道 0 除数静默产出 Infinity/NaN 伪值，守门改抛（对齐高精度路径方向）
            float divisor = other.floatValue();
            if (operation.explicitZeroDivisor && divisor == 0F) {
                throw new ArithmeticException("/ by zero");
            }
            float value = operation.floatValue.apply(one.floatValue(), divisor);
            return narrowFirstOperand ? (N) Float.valueOf(value) : as(value, one);
        }
        if (numClass.isAssignableFrom(Double.class)) {
            // D1 除零显式失败：同上，double 通道
            double divisor = other.doubleValue();
            if (operation.explicitZeroDivisor && divisor == 0D) {
                throw new ArithmeticException("/ by zero");
            }
            double value = operation.doubleValue.applyAsDouble(one.doubleValue(), divisor);
            return narrowFirstOperand ? (N) Double.valueOf(value) : as(value, one);
        }
        // 原 Short/Byte 通道分支被 findClass int 通道提升（D2）并入前路，不可达后删除；
        // 末段兜底照遗留登记第 5 项逐字保留（未知 Number 实现经 findClass Double 兜底走前路显式受控失败）
        return as(operation.doubleValue.applyAsDouble(one.doubleValue(), other.doubleValue()), one);
    }

    /**
     * 基本类型通道的六形态运算表。D2 后 integer 形态服务 int 通道（byte/short 已由 findClass 提升并入，
     * 不再作为独立通道形态存在）。
     */
    private static final class NumberOperation {

        private final BinaryOperator<BigDecimal> bigDecimal;

        private final BinaryOperator<BigInteger> bigInteger;

        private final IntBinaryOperator integer;

        private final LongBinaryOperator longValue;

        private final FloatBinaryOperator floatValue;

        private final DoubleBinaryOperator doubleValue;

        /**
         * D1 除零守门标记：divide/mod 为 true——浮点通道零除数不得产出 Infinity/NaN 伪值，
         * 双空操作数时除数侧零化后亦须当场受控失败。
         */
        private final boolean explicitZeroDivisor;

        private NumberOperation(BinaryOperator<BigDecimal> bigDecimal, BinaryOperator<BigInteger> bigInteger,
                                IntBinaryOperator integer, LongBinaryOperator longValue,
                                FloatBinaryOperator floatValue, DoubleBinaryOperator doubleValue,
                                boolean explicitZeroDivisor) {
            this.bigDecimal = bigDecimal;
            this.bigInteger = bigInteger;
            this.integer = integer;
            this.longValue = longValue;
            this.floatValue = floatValue;
            this.doubleValue = doubleValue;
            this.explicitZeroDivisor = explicitZeroDivisor;
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
            (one, other) -> one + other, false);

    private static final NumberOperation SUBTRACT = new NumberOperation(BigDecimal::subtract, BigInteger::subtract,
            (one, other) -> one - other, (one, other) -> one - other, (one, other) -> one - other,
            (one, other) -> one - other, false);

    private static final NumberOperation MULTIPLY = new NumberOperation(BigDecimal::multiply, BigInteger::multiply,
            (one, other) -> one * other, (one, other) -> one * other, (one, other) -> one * other,
            (one, other) -> one * other, false);

    private static final NumberOperation DIVIDE = new NumberOperation(
            // 现状 BigDecimal 除法定标 DECIMAL128（NumberAideTest 钉桩）
            (one, other) -> one.divide(other, MathContext.DECIMAL128), BigInteger::divide,
            (one, other) -> one / other, (one, other) -> one / other, (one, other) -> one / other,
            (one, other) -> one / other, true);

    private static final NumberOperation MOD = new NumberOperation(
            // remainder 与 Java % 语义一致（截断取余）——BigDecimal/BigInteger/基本类型三路现状同源
            BigDecimal::remainder, BigInteger::remainder,
            (one, other) -> one % other, (one, other) -> one % other, (one, other) -> one % other,
            (one, other) -> one % other, true);

    /**
     * D1 零元载体：按对侧操作数的类型通道构造数值零（null 操作数即以此参与运算）。
     * 未知数值形态（AtomicLong 等非八个标准装箱类型）兜底 int 零，随后 findClass 走 Integer 通道。
     */
    private static Number zeroLike(Number present) {
        if (present instanceof BigDecimal) {
            return BigDecimal.ZERO;
        }
        if (present instanceof BigInteger) {
            return BigInteger.ZERO;
        }
        if (present instanceof Double) {
            return Double.valueOf(0D);
        }
        if (present instanceof Float) {
            return Float.valueOf(0F);
        }
        if (present instanceof Long) {
            return Long.valueOf(0L);
        }
        if (present instanceof Short) {
            return Short.valueOf((short) 0);
        }
        if (present instanceof Byte) {
            return Byte.valueOf((byte) 0);
        }
        return Integer.valueOf(0);
    }

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
                    // D2 提升（fix-registered-defects）：byte/short 参与二元运算先提升至 int 通道（JLS 二元数值提升），
                    // 优先级仅在"是否需要更高精度通道"上参与，不再以窄通道身份回折落型
                    if (Byte.class == findClass || Short.class == findClass) {
                        return Integer.class;
                    }
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
