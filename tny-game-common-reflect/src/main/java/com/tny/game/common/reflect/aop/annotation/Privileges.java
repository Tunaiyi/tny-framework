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

package com.tny.game.common.reflect.aop.annotation;

import java.lang.reflect.*;

public enum Privileges {

    ALL {
        @Override
        public boolean check(Method method) {
            return true;
        }
    },

    PUBLIC {
        @Override
        public boolean check(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }
    },

    PROTECTED {
        @Override
        public boolean check(Method method) {
            return Modifier.isProtected(method.getModifiers());
        }
    },

    PRIVATE {
        @Override
        public boolean check(Method method) {
            return Modifier.isPrivate(method.getModifiers());
        }
    };

    public abstract boolean check(Method method);

}
