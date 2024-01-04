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

package com.tny.game.common.enums;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 4:52 下午
 */
public interface Enumerator<O> {

    <T extends O> T check(Object key, String message, Object... args);

    <T extends O> T of(Object key);

    <T extends O> Optional<T> option(Object key);

    <T extends O> Set<T> allValues();

    Set<Class<? extends O>> allClasses();

    <T extends Enum<T>> Set<Class<T>> allEnumClasses();

}
