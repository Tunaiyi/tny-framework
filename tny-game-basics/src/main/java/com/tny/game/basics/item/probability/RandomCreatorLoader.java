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

package com.tny.game.basics.item.probability;

import com.tny.game.common.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * Created by Kun Yang on 16/9/9.
 */
public final class RandomCreatorLoader {

    private RandomCreatorLoader() {
    }

    @ClassSelectorProvider
    public static ClassSelector selector() {
        return ClassSelector.create()
                .addFilter(SubOfClassFilter.ofInclude(RandomCreatorFactory.class))
                .setHandler(RandomCreatorLoader::handle);
    }

    private static void handle(Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || RandomCreators.isDefault(clazz)) {
                continue;
            }
            try {
                var object = clazz.getDeclaredConstructor().newInstance();
                if (object instanceof RandomCreatorFactory) {
                    RandomCreatorFactory<?, ?> factory = (RandomCreatorFactory<?, ?>) object;
                    factory.registerSelf();
                }
            } catch (Exception e) {
                Asserts.throwWith(IllegalArgumentException::new, e, "创建 {} 异常", clazz);
            }
        }
    }

}
