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

import org.apache.commons.lang3.StringUtils;

import static com.tny.game.namespace.NamespacePathNames.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 16:35
 **/
public class NamespacePath {

    private static final String DELIMITER = NAMESPACE_DELIMITER;

    private static final String EMPTY_NODE = "";

    private static final NamespacePath ROOT = new NamespacePath();

    private final String node;

    private final String pathname;

    private final NamespacePath parent;

    public static NamespacePath root() {
        return ROOT;
    }

    public static NamespacePath path(NamespacePath parent, String node) {
        if (parent == null) {
            if (StringUtils.isBlank(node)) {
                return ROOT;
            } else {
                return new NamespacePath(null, node);
            }
        } else {
            if (StringUtils.isBlank(node)) {
                return parent;
            } else {
                return new NamespacePath(parent, node);
            }
        }
    }

    private NamespacePath() {
        this.pathname = DELIMITER;
        this.node = EMPTY_NODE;
        this.parent = null;
    }

    private NamespacePath(NamespacePath parent, String node) {
        if (parent == null) {
            this.pathname = NamespacePathNames.dirPath(node);
            this.node = node;
            this.parent = null;
        } else {
            this.pathname = NamespacePathNames.dirPath(parent.getPathname(), node);
            this.node = node;
            this.parent = parent;
        }
    }

    /**
     * 连接节点
     *
     * @param node 节点
     * @return 分析
     */
    public NamespacePath contact(Object node) {
        var nodeValue = node.toString();
        return NamespacePath.path(this, nodeValue);
    }

    /**
     * @return 父路径
     */
    public NamespacePath parent() {
        return parent;
    }

    /**
     * @return 父路径名
     */
    public String getParentPathname() {
        return parent != null ? parent.getPathname() : null;
    }

    /**
     * @return 当前路径名
     */
    public String getPathname() {
        return pathname;
    }

    /**
     * @return 当前节点
     */
    public String getNode() {
        return node;
    }

    /**
     * @return 是否是根路径
     */
    public boolean isRoot() {
        return parent == null;
    }

}
