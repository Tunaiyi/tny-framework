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