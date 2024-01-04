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

package com.tny.game.data;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.data.annotation.*;
import com.tny.game.data.cache.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;

/**
 * 实体方案加载器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class EntitySchemeLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySchemeLoader.class);

    private static final Set<Class<?>> cacheObjectClasses = new ConcurrentHashSet<>();

    private static final Set<EntityScheme> cacheSchemes = new ConcurrentHashSet<>();

    private static final Map<Class<?>, EntityScheme> cacheSchemeMap = new CopyOnWriteMap<>();

    @ClassSelectorProvider
    static ClassSelector cacheObjectSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(EntityObject.class))
                .setHandler((classes) -> {
                    cacheObjectClasses.addAll(classes);
                    registerScheme(classes);
                    LOGGER.info("DataClassLoader.CacheObject : {}", cacheObjectClasses.size());
                });
    }

    public static Set<Class<?>> getAllCacheEntityClasses() {
        return Collections.unmodifiableSet(cacheObjectClasses);
    }

    public static Set<EntityScheme> getAllCacheSchemes() {
        return Collections.unmodifiableSet(cacheSchemes);
    }

    public static EntityScheme getCacheScheme(Class<?> clazz) {
        return cacheSchemeMap.get(clazz);
    }

    private static void registerScheme(Collection<Class<?>> classes) {
        cacheObjectClasses.addAll(classes);
        Map<Class<?>, EntityScheme> schemeMap = new HashMap<>();
        for (Class<?> clazz : classes) {
            parseScheme(clazz, schemeMap);
        }
        cacheSchemes.addAll(schemeMap.values());
        cacheSchemeMap.putAll(schemeMap);
    }

    private static EntityScheme parseScheme(Class<?> clazz, Map<Class<?>, EntityScheme> schemeMap) {
        EntityScheme scheme = schemeMap.get(clazz);
        if (scheme != null) {
            return scheme;
        }
        scheme = new EntityScheme(clazz);
        if (scheme.isCacheSelf()) {
            schemeMap.put(scheme.getEntityClass(), scheme);
            return scheme;
        } else {
            scheme = parseScheme(scheme.getCacheClass(), schemeMap);
            schemeMap.put(clazz, scheme);
        }
        return scheme;
    }

}
