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
