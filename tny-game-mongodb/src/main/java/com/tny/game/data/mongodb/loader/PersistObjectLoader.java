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

package com.tny.game.data.mongodb.loader;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

import static com.tny.game.codec.jackson.mapper.AutoRegisterModuleClassesHandler.*;

/**
 * <p>
 */
public final class PersistObjectLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(PersistObjectLoader.class);

    private static final Set<Class<?>> CONVERTER_CLASSES = new ConcurrentHashSet<>();

    private PersistObjectLoader() {
    }

    @ClassSelectorProvider
    static ClassSelector mixDocumentSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(Document.class))
                .setHandler(createHandler((module, classes) ->
                        //							module.setMixInAnnotation(docClass, MongoIdMix.class);
                        CONVERTER_CLASSES.addAll(classes)));
    }

    public static Set<Class<?>> getConverterClasses() {
        return Collections.unmodifiableSet(CONVERTER_CLASSES);
    }

}
