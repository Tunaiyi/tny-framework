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
