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

package com.tny.game.boot.common;

import com.tny.game.common.lifecycle.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.common.type.*;
import com.tny.game.scanner.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 1:12 下午
 */
@AsLifecycle
public class ResultCodeLoadInitializer {

    @StaticInit
    static <E extends Enum<E> & ResultCode> void loadClass() {
        ReferenceType<Class<E>> type = new ReferenceType<Class<E>>() {

        };
        AutoLoadClasses.getClasses(ResultCode.class)
                .stream()
                .map(c -> as(c, type))
                .forEach(ResultCodes::registerClass);
    }

}
