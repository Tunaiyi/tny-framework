/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.persistent;

import com.tny.game.basics.auto.*;
import com.tny.game.basics.persistent.annotation.*;

import java.lang.reflect.Method;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class AutoManageMethod extends AutoMethod<Modifiable, ModifiableReturn, ModifiableParam, Immutable> {

    protected AutoManageMethod(Method method) {
        super(method, Modifiable.class, ModifiableReturn.class, ModifiableParam.class, Immutable.class);
    }

}
