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
package com.tny.game.common.reflect;

import com.tny.game.common.reflect.aop.annotation.AOP;
import com.tny.game.common.reflect.aop.annotation.Privileges;

/**
 * 顶层切面目标（@AOP 值参数限定 PUBLIC）——供 ProxyAccessorIntegrityTest 使用。
 */
@AOP(Privileges.PUBLIC)
public class AopGuardedTarget {

    public int publicCalls;

    public int protectedCalls;

    protected void prot() {
        protectedCalls++;
    }

    public void pub() {
        publicCalls++;
    }

}

class AopGuardedTargetAccessors {

    static void callProtected(AopGuardedTarget proxy) throws Exception {
        AopGuardedTarget.class.getDeclaredMethod("prot").invoke(proxy);
    }
}
