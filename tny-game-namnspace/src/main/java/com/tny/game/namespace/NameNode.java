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
