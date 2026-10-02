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
            return StringUtils.isBlank(node) ? ROOT : new NamespacePath(null, node);
        }
        return StringUtils.isBlank(node) ? parent : new NamespacePath(parent, node);
    }

    private NamespacePath() {
        this.pathname = DELIMITER;
        this.node = EMPTY_NODE;
        this.parent = null;
    }

    private NamespacePath(NamespacePath parent, String node) {
        this.parent = parent;
        this.node = node;
        this.pathname = parent == null ? NamespacePathNames.dirPath(node) : NamespacePathNames.dirPath(parent.getPathname(), node);
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
