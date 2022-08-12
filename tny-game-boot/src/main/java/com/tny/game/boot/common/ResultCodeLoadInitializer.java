/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
