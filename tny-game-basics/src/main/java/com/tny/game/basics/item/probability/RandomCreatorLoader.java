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
