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
