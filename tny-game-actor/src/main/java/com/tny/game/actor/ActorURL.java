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

package com.tny.game.actor;

import java.util.regex.Pattern;

/**
 * Actor路径对象
 *
 * @author KGTny
 */
public abstract class ActorURL {

    public static String ELEMENT_REGEX_STR = "(?:[-\\w:@&=+,.!~*'_;]|%\\p{XDigit}{2})(?:[-\\w:@&=+,.!~*'$_;]|%\\p{XDigit}{2})*";

    public static Pattern ELEMENT_REGEX = Pattern.compile(ELEMENT_REGEX_STR);

    /**
     * @return 获取ActorID
     */
    public abstract int getAid();

    /**
     * @return 获取当前路径的叶子节点名字
     */
    public abstract String getName();

    /**
     * 当前路径创建指定aid的ActorPath
     *
     * @param aid
     * @return
     */
    public abstract ActorURL withUid(long aid);

    /**
     * @return 获取父路径对象
     */
    public abstract ActorURL getParent();

    /**
     * 获取当前路径下,child为子接点路径的ActorPath
     *
     * @param child
     * @return child的ActorPath
     */
    public abstract ActorURL child(Iterable<String> child);

    /**
     * 获取当前路径下,child为子接点路径的ActorPath
     *
     * @param child
     * @return child的ActorPath
     */
    public abstract ActorURL child(String child);

    /**
     * 子接点
     *
     * @return
     */
    public abstract Iterable<String> getElements();

    /**
     * @return 获取根节点
     */
    public abstract ActorURL root();

    /**
     * 是否是根
     *
     * @return
     */
    public abstract boolean isRoot();

    /**
     * @return 返回不包含网络地址的路径信息
     * /parent/child
     */
    public abstract String toStringWithoutAddress();

    /**
     * @param address 指定的网络地址
     * @return 返回含address网络地址的路径信息
     * <protocol>://<system>@<host>:<port>/parent/child
     */
    public abstract String toStringWithAddress(ActorAddress address);

    /**
     * @return 返回当前根接点地址为Address字符串构造全路径字符串
     * <protocol>://<system>@<host>:<port>/<parent>/<child>#<AID>
     */
    public abstract String toSerializationFormat();

    /**
     * @param address 指定的网络地址
     * @return 返回指定的网络地址为Address字符串构造全路径字符串
     * <protocol>://<system>@<host>:<port>/<parent>/<child>#<AID>
     */
    public abstract String toSerializationFormat(ActorAddress address);

    /**
     * 获取地址
     *
     * @return
     */
    public abstract ActorAddress getAddress();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof ActorURL actorPath)) {
            return false;
        }
        if (!getName().equals(actorPath.getName())) {
            return false;
        }
        return getParent().equals(actorPath.getParent());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

}