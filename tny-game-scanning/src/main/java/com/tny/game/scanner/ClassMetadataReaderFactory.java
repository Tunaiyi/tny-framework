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

package com.tny.game.scanner;

import org.springframework.core.io.support.*;
import org.springframework.core.type.classreading.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/2 3:28 上午
 */
public final class ClassMetadataReaderFactory {

    private static MetadataReaderFactory readerFactory;// = new CachingMetadataReaderFactory(resourcePatternResolver);

    public static void init(ClassLoader classLoader) {
        // = new PathMatchingResourcePatternResolver();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
        readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
    }

    public static MetadataReaderFactory getFactory() {
        return readerFactory;
    }

    public static MetadataReaderFactory createReaderFactory(ClassLoader classLoader) {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
        readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        return readerFactory;
    }

}
