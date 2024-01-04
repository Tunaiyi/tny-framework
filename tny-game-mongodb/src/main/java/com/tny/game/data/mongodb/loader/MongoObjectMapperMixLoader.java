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
