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
 * 多可见性声明探针目标（@AOP({PUBLIC, PROTECTED})）——供 ProxyAccessorIntegrityTest
 * 「多可见性声明按集合精确生效」场景使用：公开与受 protected 方法应被拦截，包私有不被拦截。
 * 顶层类（AOP 代理生成对嵌套类命名不支持，同 AopGuardedTarget）。
 */
@AOP({Privileges.PUBLIC, Privileges.PROTECTED})
public class AopMultiVisibleTarget {

    public int publicCalls;

    public int protectedCalls;

    public int packageCalls;

    public void pub() {
        publicCalls++;
    }

    protected void prot() {
        protectedCalls++;
    }

    void pkg() {
        packageCalls++;
    }

}
