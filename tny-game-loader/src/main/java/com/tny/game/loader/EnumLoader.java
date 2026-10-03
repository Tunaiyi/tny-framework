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

package com.tny.game.loader;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;

/**
 * 读取 tny-factory.properties 配置中
 * com.tny.game.loader.EnumLoader 配置相关的枚举会提前读取
 * Created by Kun Yang on 16/9/9.
 */
public final class EnumLoader {

    private EnumLoader() {
    }

    @SuppressWarnings("unchecked")
    @ClassSelectorProvider
    public static <E extends Enum<E>> ClassSelector selector() {
        return ClassSelector.create()
                .addFilter(SubOfClassFilter.ofInclude(AutoLoadClasses.getClasses(EnumLoader.class)))
                .setHandler((classes) -> classes.stream()
                        .filter(Class::isEnum)
                        .map(c -> (Class<E>) c)
                        .forEach(EnumUtils::getEnumList)
                );
    }

}
