package com.tny.game.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Logs {

    public static final Logger LOGGER = LoggerFactory.getLogger(Logs.class);

    public static final String LOADER = "configLoader";
    public static final String EVENT = "commonEvent";
    public static final String LOCK = "commonLock";
    public static final String FILE_MONITOR = "fileMonitor";
    public static final String TIME_TASK = "timeTask";
    public static final String WORKER = "worker";

    private static final char DELIM_START = '{';
    private static final char ESCAPE_CHAR = '\\';
    private static final String DELIM_STR = "{}";

    public static Object[] msg(Object... objects) {
        return objects;
    }

    public static byte trace(byte value) {
        return trace(LOGGER, null, value);
    }

    public static byte trace(Logger logger, byte value) {
        return trace(logger, null, value);
    }

    public static byte trace(Object key, byte value) {
        return trace(LOGGER, key, value);
    }


    public static byte trace(Logger logger, Object key, byte value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static short trace(short value) {
        return trace(LOGGER, null, value);
    }

    public static short trace(Logger logger, short value) {
        return trace(logger, null, value);
    }

    public static short trace(Object key, short value) {
        return trace(LOGGER, key, value);
    }


    public static short trace(Logger logger, Object key, short value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static int trace(int value) {
        return trace(LOGGER, null, value);
    }

    public static int trace(Logger logger, int value) {
        return trace(logger, null, value);
    }

    public static int trace(Object key, int value) {
        return trace(LOGGER, key, value);
    }

    public static int trace(Logger logger, Object key, int value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static long trace(long value) {
        return trace(LOGGER, null, value);
    }

    public static long trace(Logger logger, long value) {
        return trace(logger, null, value);
    }

    public static long trace(Object key, long value) {
        return trace(LOGGER, key, value);
    }

    public static long trace(Logger logger, Object key, long value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static float trace(float value) {
        return trace(LOGGER, null, value);
    }

    public static float trace(Logger logger, float value) {
        return trace(logger, null, value);
    }

    public static float trace(Object key, float value) {
        return trace(LOGGER, key, value);
    }


    public static float trace(Logger logger, Object key, float value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }


    public static double trace(double value) {
        return trace(LOGGER, null, value);
    }

    public static double trace(Logger logger, double value) {
        return trace(logger, null, value);
    }

    public static double trace(Object key, double value) {
        return trace(LOGGER, key, value);
    }


    public static double trace(Logger logger, Object key, double value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static boolean trace(boolean value) {
        return trace(LOGGER, null, value);
    }

    public static boolean trace(Logger logger, boolean value) {
        return trace(logger, null, value);
    }

    public static boolean trace(Object key, boolean value) {
        return trace(LOGGER, key, value);
    }

    public static boolean trace(Logger logger, Object key, boolean value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static <T> T trace(T value) {
        return trace(LOGGER, null, value);
    }

    public static <T> T trace(Logger logger, T value) {
        return trace(logger, null, value);
    }

    public static <T> T trace(Object key, T value) {
        return trace(LOGGER, key, value);
    }

    public static <T> T trace(Logger logger, Object key, T value) {
        if (logger.isDebugEnabled())
            logger.debug(key == null ? "{}" : key + " = {}", value);
        return value;
    }

    public static String format(final String messagePattern, final Object... argArray) {
        if (messagePattern == null || argArray == null || argArray.length == 0) {
            return messagePattern;
        }

        int i = 0;
        int delimIndex;
        StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);

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
                    sbuf.append(messagePattern.substring(i, messagePattern.length()));
                    return sbuf.toString();
                }
            } else {
                if (isEscapedDelimeter(messagePattern, delimIndex)) {
                    if (!isDoubleEscaped(messagePattern, delimIndex)) {
                        index--; // DELIM_START was escaped, thus should not be incremented
                        sbuf.append(messagePattern.substring(i, delimIndex - 1));
                        sbuf.append(DELIM_START);
                        i = delimIndex + 1;
                    } else {
                        // The escape character preceding the delimiter start is
                        // itself escaped: "abc x:\\{}"
                        // we have to consume one backward slash
                        sbuf.append(messagePattern.substring(i, delimIndex - 1));
                        deeplyAppendParameter(sbuf, argArray[index], new HashMap<>());
                        i = delimIndex + 2;
                    }
                } else {
                    // normal case
                    sbuf.append(messagePattern.substring(i, delimIndex));
                    deeplyAppendParameter(sbuf, argArray[index], new HashMap<>());
                    i = delimIndex + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        sbuf.append(messagePattern.substring(i, messagePattern.length()));
        if (index < argArray.length - 1) {
            return sbuf.toString();
        } else {
            return sbuf.toString();
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
    //	public static String format(Object key, Object ... values) {
    //		StringBuilder builder = new StringBuilder();
    //		String [] value = StringUtils.split(message,"{}");
    //	}

}
