package com.tny.game.namespace;

/**
 * 命名空间路径
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 03:27
 **/
public class NamespacePaths {

    private static final String NAMESPACE_DELIMITER = "/";

    public static String nodePath(String parent, Object... nodes) {
        boolean endDelimiter = parent.endsWith(NAMESPACE_DELIMITER);
        boolean startsWith = parent.startsWith(NAMESPACE_DELIMITER);
        StringBuilder builder = new StringBuilder();
        if (!startsWith) {
            builder.append(NAMESPACE_DELIMITER);
        }
        builder.append(parent);
        if (endDelimiter) {
            builder.deleteCharAt(builder.length() - 1);
        }
        for (Object node : nodes) {
            builder.append(NAMESPACE_DELIMITER).append(node);
        }
        return builder.toString();
    }

    public static String dirPath(String parent, Object... nodes) {
        boolean endDelimiter = parent.endsWith(NAMESPACE_DELIMITER);
        boolean startsWith = parent.startsWith(NAMESPACE_DELIMITER);
        StringBuilder builder = new StringBuilder();
        if (!startsWith) {
            builder.append(NAMESPACE_DELIMITER);
        }
        builder.append(parent);
        if (!endDelimiter) {
            builder.append(NAMESPACE_DELIMITER);
        }
        for (Object node : nodes) {
            builder.append(node).append(NAMESPACE_DELIMITER);
        }
        return builder.toString();
    }

}
