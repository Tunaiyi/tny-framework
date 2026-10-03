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
package com.tny.game.data.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 3:08 下午
 */
public class AnnotationCacheKeyMakerFactory implements CacheKeyMakerFactory {

    public static final String MAKER_NAME = "annotationCacheKeyMakerFactory";

    private static final Map<Class<?>, CacheKeyMaker<?, ?>> makerMap = new ConcurrentHashMap<>();

    @Override
    public CacheKeyMaker<?, ?> createMaker(EntityScheme scheme) {
        Class<?> entityClass = scheme.getEntityClass();
        return makerMap.computeIfAbsent(entityClass, AnnotationCacheKeyMaker::new);
    }

}
