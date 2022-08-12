/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace;

/**
 * 命名空间路径
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 03:27
 **/
public class NamespacePathNames {

    public static final String NAMESPACE_DELIMITER = "/";

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
