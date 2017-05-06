package com.tny.game.number;

import com.tny.game.LogUtils;

/**
 * Created by Kun Yang on 16/2/17.
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    @SuppressWarnings("unchecked")
    public static <N extends Number> N become(Number source, N target) {
        Number value;
        if (target instanceof Integer)
            value = source.intValue();
        else if (target instanceof Long)
            value = source.longValue();
        else if (target instanceof Float)
            value = source.floatValue();
        else if (target instanceof Double)
            value = source.doubleValue();
        else if (target instanceof Short)
            value = source.shortValue();

        else if (target instanceof Byte)
            value = source.byteValue();
        else
            value = source.intValue();
        return (N) value;
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N as(Object source, Class<N> clazz) {
        Number value;
        if (!(source instanceof Number))
            throw new ClassCastException(LogUtils.format("{} {} 不属于 {}", source, source.getClass(), Number.class));
        value = (Number) source;
        if (Integer.class == clazz)
            value = value.intValue();
        else if (Long.class == clazz)
            value = value.longValue();
        else if (Float.class == clazz)
            value = value.floatValue();
        else if (Double.class == clazz)
            value = value.doubleValue();
        else if (Short.class == clazz)
            value = value.shortValue();
        else if (Byte.class == clazz)
            value = value.byteValue();
        else
            value = value.intValue();
        return (N) value;
    }

    public static <N extends Number> N add(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return become(one.intValue() + other.intValue(), one);
        if (numClass.isAssignableFrom(Long.class))
            return become(one.longValue() + other.longValue(), one);
        if (numClass.isAssignableFrom(Float.class))
            return become(one.floatValue() + other.floatValue(), one);
        if (numClass.isAssignableFrom(Double.class))
            return become(one.doubleValue() + other.doubleValue(), one);
        if (numClass.isAssignableFrom(Short.class))
            return become(one.shortValue() + other.shortValue(), one);
        if (numClass.isAssignableFrom(Byte.class))
            return become(one.byteValue() + other.byteValue(), one);
        return become(one.doubleValue() + other.doubleValue(), one);
    }

    public static <N extends Number> N sub(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return become(one.intValue() - other.intValue(), one);
        if (numClass.isAssignableFrom(Long.class))
            return become(one.longValue() - other.longValue(), one);
        if (numClass.isAssignableFrom(Float.class))
            return become(one.floatValue() - other.floatValue(), one);
        if (numClass.isAssignableFrom(Double.class))
            return become(one.doubleValue() - other.doubleValue(), one);
        if (numClass.isAssignableFrom(Short.class))
            return become(one.shortValue() - other.shortValue(), one);
        if (numClass.isAssignableFrom(Byte.class))
            return become(one.byteValue() - other.byteValue(), one);
        return become(one.doubleValue() - other.doubleValue(), one);
    }

    public static <N extends Number> N multiply(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return become(one.intValue() * other.intValue(), one);
        if (numClass.isAssignableFrom(Long.class))
            return become(one.longValue() * other.longValue(), one);
        if (numClass.isAssignableFrom(Float.class))
            return become(one.floatValue() * other.floatValue(), one);
        if (numClass.isAssignableFrom(Double.class))
            return become(one.doubleValue() * other.doubleValue(), one);
        if (numClass.isAssignableFrom(Short.class))
            return become(one.shortValue() * other.shortValue(), one);
        if (numClass.isAssignableFrom(Byte.class))
            return become(one.byteValue() * other.byteValue(), one);
        return become(one.doubleValue() * other.doubleValue(), one);
    }

    public static <N extends Number> double divideAsDouble(N one, N other) {
        if (one == null)
            return other.floatValue();
        if (other == null)
            return one.floatValue();
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        return as(one.doubleValue() / other.doubleValue(), Double.class);
    }

    public static <N extends Number> float divideAsFloat(N one, N other) {
        if (one == null)
            return other.floatValue();
        if (other == null)
            return one.floatValue();
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class) || numClass.isAssignableFrom(Float.class) || numClass.isAssignableFrom(Short.class) || numClass.isAssignableFrom(Byte.class))
            return as(one.floatValue() / other.floatValue(), Float.class);
        if (numClass.isAssignableFrom(Long.class) || numClass.isAssignableFrom(Double.class))
            return as(one.doubleValue() / other.doubleValue(), Float.class);
        return as(one.doubleValue() / other.doubleValue(), Float.class);
    }

    public static <N extends Number> N divide(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return become(one.intValue() / other.intValue(), one);
        if (numClass.isAssignableFrom(Long.class))
            return become(one.longValue() / other.longValue(), one);
        if (numClass.isAssignableFrom(Float.class))
            return become(one.floatValue() / other.floatValue(), one);
        if (numClass.isAssignableFrom(Double.class))
            return become(one.doubleValue() / other.doubleValue(), one);
        if (numClass.isAssignableFrom(Short.class))
            return become(one.shortValue() / other.shortValue(), one);
        if (numClass.isAssignableFrom(Byte.class))
            return become(one.byteValue() / other.byteValue(), one);
        return become(one.doubleValue() / other.doubleValue(), one);
    }

    public static <N extends Number> N mod(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return become(one.intValue() % other.intValue(), one);
        if (numClass.isAssignableFrom(Long.class))
            return become(one.longValue() % other.longValue(), one);
        if (numClass.isAssignableFrom(Float.class))
            return become(one.floatValue() % other.floatValue(), one);
        if (numClass.isAssignableFrom(Double.class))
            return become(one.doubleValue() % other.doubleValue(), one);
        if (numClass.isAssignableFrom(Short.class))
            return become(one.shortValue() % other.shortValue(), one);
        if (numClass.isAssignableFrom(Byte.class))
            return become(one.byteValue() % other.byteValue(), one);
        return become(one.doubleValue() % other.doubleValue(), one);
    }

    public static boolean less(Number one, Number other) {
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return one.intValue() < other.intValue();
        if (numClass.isAssignableFrom(Long.class))
            return one.longValue() < other.longValue();
        if (numClass.isAssignableFrom(Float.class))
            return one.floatValue() < other.floatValue();
        if (numClass.isAssignableFrom(Double.class))
            return one.doubleValue() < other.doubleValue();
        if (numClass.isAssignableFrom(Short.class))
            return one.shortValue() < other.shortValue();
        if (numClass.isAssignableFrom(Byte.class))
            return one.byteValue() < other.byteValue();
        return one.doubleValue() < other.doubleValue();
    }


    public static boolean lessEqual(Number one, Number other) {
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return one.intValue() <= other.intValue();
        if (numClass.isAssignableFrom(Long.class))
            return one.longValue() <= other.longValue();
        if (numClass.isAssignableFrom(Float.class))
            return one.floatValue() <= other.floatValue();
        if (numClass.isAssignableFrom(Double.class))
            return one.doubleValue() <= other.doubleValue();
        if (numClass.isAssignableFrom(Short.class))
            return one.shortValue() <= other.shortValue();
        if (numClass.isAssignableFrom(Byte.class))
            return one.byteValue() <= other.byteValue();
        return one.doubleValue() <= other.doubleValue();
    }


    public static boolean greater(Number one, Number other) {
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return one.intValue() > other.intValue();
        if (numClass.isAssignableFrom(Long.class))
            return one.longValue() > other.longValue();
        if (numClass.isAssignableFrom(Float.class))
            return one.floatValue() > other.floatValue();
        if (numClass.isAssignableFrom(Double.class))
            return one.doubleValue() > other.doubleValue();
        if (numClass.isAssignableFrom(Short.class))
            return one.shortValue() > other.shortValue();
        if (numClass.isAssignableFrom(Byte.class))
            return one.byteValue() > other.byteValue();
        return one.doubleValue() > other.doubleValue();
    }

    public static boolean greaterEqual(Number one, Number other) {
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return one.intValue() >= other.intValue();
        if (numClass.isAssignableFrom(Long.class))
            return one.longValue() >= other.longValue();
        if (numClass.isAssignableFrom(Float.class))
            return one.floatValue() >= other.floatValue();
        if (numClass.isAssignableFrom(Double.class))
            return one.doubleValue() >= other.doubleValue();
        if (numClass.isAssignableFrom(Short.class))
            return one.shortValue() >= other.shortValue();
        if (numClass.isAssignableFrom(Byte.class))
            return one.byteValue() >= other.byteValue();
        return one.doubleValue() >= other.doubleValue();
    }


    public static boolean equal(Number one, Number other) {
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class))
            return one.intValue() == other.intValue();
        if (numClass.isAssignableFrom(Long.class))
            return one.longValue() == other.longValue();
        if (numClass.isAssignableFrom(Float.class))
            return one.floatValue() == other.floatValue();
        if (numClass.isAssignableFrom(Double.class))
            return one.doubleValue() == other.doubleValue();
        if (numClass.isAssignableFrom(Short.class))
            return one.shortValue() == other.shortValue();
        if (numClass.isAssignableFrom(Byte.class))
            return one.byteValue() == other.byteValue();
        return one.doubleValue() == other.doubleValue();
    }

    public static int compare(Number one, Number other) {
        Class<?> numClass = findClass(one.getClass(), other.getClass());
        if (numClass.isAssignableFrom(Integer.class) || numClass.isAssignableFrom(Short.class) || numClass.isAssignableFrom(Byte.class))
            return one.intValue() - other.intValue();
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

    private static final Class<?>[] NUM_CLASSES = new Class<?>[]{Double.class, Float.class, Long.class, Integer.class, Short.class, Byte.class};

    private static Class<?> findClass(Class<?>... classes) {
        for (Class<?> clazz : NUM_CLASSES) {
            for (Class<?> findClass : classes) {
                if (clazz.isAssignableFrom(findClass))
                    return findClass;
            }
        }
        return NUM_CLASSES[0];
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(byte x, byte y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }


    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(byte x, byte y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }


    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(short x, short y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(short x, short y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(int x, int y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(long x, long y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(float x, float y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(float x, float y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }

    /**
     * 升序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : -1 | x = y : 0 | x ></> y : 1
     */
    public static int ascCompare(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     * 降序比较
     *
     * @param x 参数x
     * @param y 参数y
     * @return x < y : 1 | x = y : 0 | x ></> y : -1
     */
    public static int desCompare(double x, double y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }

}
