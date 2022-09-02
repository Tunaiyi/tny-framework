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
package com.tny.game.namespace;

/**
 * 命名空间节点
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 04:16
 **/
public class NameNode<T> {

    private final String name;

    private final long id;

    private final T value;

    private final long version;

    private final long revision;

    private final boolean delete;

    public NameNode(String name, long id, T value, long version, long revision, boolean delete) {
        this.name = name;
        this.id = id;
        this.value = value;
        this.version = version;
        this.revision = revision;
        this.delete = delete;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    public long getVersion() {
        return version;
    }

    public long getRevision() {
        return revision;
    }

    public boolean isDelete() {
        return this.delete;
    }

    @Override
    public String toString() {
        return "NameNode{" + "name=" + name +
                ", id=" + id +
                ", version=" + version +
                ", revision=" + revision +
                '}';
    }

}
