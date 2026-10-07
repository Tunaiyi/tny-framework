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
