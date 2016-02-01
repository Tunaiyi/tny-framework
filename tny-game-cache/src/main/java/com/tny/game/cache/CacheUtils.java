package com.tny.game.cache;

public class CacheUtils {

    /**
     * 分隔符
     */
    public static final String SEPARATOR = ":";

    private static final String KEY_SEPARATOR = "tny.cache.key.separator";
    private static final String KEY_HEAD_PR_KEY = "tny.cache.key.pr";

    public static String getSeparator() {
        return System.getProperty(KEY_SEPARATOR, CacheUtils.SEPARATOR);
    }

    public static String getKeyHead(String prefix) {
        return getKeyHeadPre() + prefix;
    }

    public static String getKeyHeadPre() {
        return System.getProperty(KEY_HEAD_PR_KEY, "");
    }

    public static String getKey(String prefix, Object... keyValues) {
        StringBuilder builder = new StringBuilder(45);
        builder.append(getKeyHead(prefix));
        String separator = getSeparator();
        for (Object value : keyValues) {
            builder.append(separator)
                    .append(value);
        }
        return builder.toString();
    }
}
