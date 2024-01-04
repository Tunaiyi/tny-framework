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
