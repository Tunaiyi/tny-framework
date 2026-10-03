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

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.data.mongodb.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class MongoObjectMapperMixLoader {

    private static final SimpleModule module = new SimpleModule();

    @ClassSelectorProvider
    static ClassSelector autoMixClassesSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(MongoJsonAutoMixClasses.class))
                .setHandler((classes) -> classes.forEach(mix -> {
                            MongoJsonAutoMixClasses mixClasses = mix.getAnnotation(MongoJsonAutoMixClasses.class);
                            for (Class<?> mixClass : mixClasses.value()) {
                                module.setMixInAnnotation(mixClass, mix);
                            }
                        })
                );
    }

    public static Module getModule() {
        return module;
    }

}
