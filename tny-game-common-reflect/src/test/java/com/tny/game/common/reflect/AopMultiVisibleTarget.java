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
