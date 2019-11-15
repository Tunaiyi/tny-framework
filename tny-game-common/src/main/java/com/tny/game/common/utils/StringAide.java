package com.tny.game.common.utils;

import java.util.*;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Created by Kun Yang on 16/3/9.
 */
public class StringAide {


    private static final char DELIM_START = '{';
    private static final char ESCAPE_CHAR = '\\';
    private static final String DELIM_STR = "{}";

    /**
     * 如果 value is blank 返回 value, 否则返回elseValue
     *
     * @param value     值
     * @param elseValue 其他值
     * @return value is blank 返回 value, 否则返回elseValue
     */
    public static String ifBlankElse(String value, String elseValue) {
        if (isBlank(value))
            return value;
        return elseValue;
    }

    /**
     * 如果 value is blank 返回 value, 否则返回elseValue.get()
     *
     * @param value     值
     * @param elseValue 其他值提供者
     * @return value is blank 返回 value, 否则返回elseValue.get()
     */
    public static String ifBlankElse(String value, Supplier<String> elseValue) {
        if (isBlank(value))
            return value;
        return elseValue.get();
    }

    /**
     * 如果 value is not blank 返回 value, 否则返回elseValue
     *
     * @param value     值
     * @param elseValue 其他值
     * @return value is not blank 返回 value, 否则返回elseValue
     */
    public static String ifNotBlankElse(String value, String elseValue) {
        if (isNoneBlank(value))
            return value;
        return elseValue;
    }


    /**
     * 如果 value is not blank 返回 value, 否则返回elseValue.get()
     *
     * @param value     值
     * @param elseValue 其他值提供者
     * @return value is not blank 返回 value, 否则返回elseValue.get()
     */
    public static String ifNotBlankElse(String value, Supplier<String> elseValue) {
        if (isNoneBlank(value))
            return value;
        return elseValue.get();
    }

    public static String format(final String messagePattern, final Object... argArray) {
        if (messagePattern == null || argArray == null || argArray.length == 0) {
            return messagePattern;
        }

        int i = 0;
        int delimIndex;
        StringBuffer buffer = new StringBuffer(messagePattern.length() + 50);

        int index;
        for (index = 0; index < argArray.length; index++) {

            delimIndex = messagePattern.indexOf(DELIM_STR, i);

            if (delimIndex == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return messagePattern;
                } else {
                    // add the tail string which contains no variables and return
                    // the result.
                    buffer.append(messagePattern.substring(i, messagePattern.length()));
                    return buffer.toString();
                }
            } else {
                if (isEscapedDelimeter(messagePattern, delimIndex)) {
                    if (!isDoubleEscaped(messagePattern, delimIndex)) {
                        index--; // DELIM_START was escaped, thus should not be incremented
                        buffer.append(messagePattern.substring(i, delimIndex - 1));
                        buffer.append(DELIM_START);
                        i = delimIndex + 1;
                    } else {
                        // The escape character preceding the delimiter start is
                        // itself escaped: "abc x:\\{}"
                        // we have to consume one backward slash
                        buffer.append(messagePattern.substring(i, delimIndex - 1));
                        deeplyAppendParameter(buffer, argArray[index], new HashMap<>());
                        i = delimIndex + 2;
                    }
                } else {
                    // normal case
                    buffer.append(messagePattern.substring(i, delimIndex));
                    deeplyAppendParameter(buffer, argArray[index], new HashMap<>());
                    i = delimIndex + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        buffer.append(messagePattern.substring(i, messagePattern.length()));
        if (index < argArray.length - 1) {
            return buffer.toString();
        } else {
            return buffer.toString();
        }
    }

    static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        return ESCAPE_CHAR == potentialEscape;
    }

    static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
    }

    private static void deeplyAppendParameter(StringBuffer buff, Object o, Map<Object, Object> seenMap) {
        if (o == null) {
            buff.append("null");
            return;
        }
        if (!o.getClass().isArray()) {
            safeObjectAppend(buff, o);
        } else {
            // check for primitive array types because they
            // unfortunately cannot be cast to Object[]
            if (o instanceof boolean[]) {
                booleanArrayAppend(buff, (boolean[]) o);
            } else if (o instanceof byte[]) {
                byteArrayAppend(buff, (byte[]) o);
            } else if (o instanceof char[]) {
                charArrayAppend(buff, (char[]) o);
            } else if (o instanceof short[]) {
                shortArrayAppend(buff, (short[]) o);
            } else if (o instanceof int[]) {
                intArrayAppend(buff, (int[]) o);
            } else if (o instanceof long[]) {
                longArrayAppend(buff, (long[]) o);
            } else if (o instanceof float[]) {
                floatArrayAppend(buff, (float[]) o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(buff, (double[]) o);
            } else {
                objectArrayAppend(buff, (Object[]) o, seenMap);
            }
        }
    }

    private static void safeObjectAppend(StringBuffer sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (Throwable t) {
            System.err
                    .println("SLF4J: Failed toString() invocation on an object of type ["
                             + o.getClass().getName() + "]");
            t.printStackTrace();
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuffer sbuf, Object[] a, Map<Object, Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            final int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1)
                    sbuf.append(", ");
            }
            // allow repeats in siblings
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuffer sbuf, boolean[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuffer sbuf, byte[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuffer sbuf, char[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuffer sbuf, short[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuffer sbuf, int[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuffer sbuf, long[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuffer sbuf, float[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuffer sbuf, double[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1)
                sbuf.append(", ");
        }
        sbuf.append(']');
    }
}
