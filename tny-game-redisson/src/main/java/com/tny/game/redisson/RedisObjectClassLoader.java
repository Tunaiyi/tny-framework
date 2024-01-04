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

package com.tny.game.redisson;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.redisson.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-02-08 06:35
 */
public class RedisObjectClassLoader {

    private static final Set<Class<?>> CONVERTER_CLASSES = new ConcurrentHashSet<>();

    private static final Set<RedisObjectRegistrar> REGISTRARS = new ConcurrentHashSet<>();

    @ClassSelectorProvider
    static ClassSelector redisObjectSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(RedisObject.class))
                .setHandler(CONVERTER_CLASSES::addAll);
    }

    @ClassSelectorProvider
    static ClassSelector redisRegistrarSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(RedisObjectRegistrar.class))
                .setHandler((classes) ->
                        classes.forEach(c -> {
                            RedisObjectRegistrar registrar = c.getAnnotation(RedisObjectRegistrar.class);
                            if (registrar != null) {
                                REGISTRARS.add(registrar);
                            }
                        }));
    }

    public static Set<Class<?>> getAllClasses() {
        return Collections.unmodifiableSet(CONVERTER_CLASSES);
    }

    public static Set<RedisObjectRegistrar> getAllRegistrars() {
        return Collections.unmodifiableSet(REGISTRARS);
    }

}

