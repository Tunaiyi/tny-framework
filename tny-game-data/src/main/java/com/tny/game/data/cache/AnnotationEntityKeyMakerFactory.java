/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
public class AnnotationEntityKeyMakerFactory implements EntityKeyMakerFactory {

    public static final String MAKER_NAME = "annotationEntityKeyMakerFactory";

    private static final Map<Class<?>, EntityKeyMaker<?, ?>> makerMap = new ConcurrentHashMap<>();

    @Override
    public EntityKeyMaker<?, ?> createMaker(EntityScheme scheme) {
        Class<?> entityClass = scheme.getEntityClass();
        return makerMap.computeIfAbsent(entityClass, AnnotationEntityKeyMaker::new);
    }

}
