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
package com.tny.game.common.lifecycle;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PrepareStarter extends Lifecycle<PrepareStarter, AppPrepareStart> {

    public static PrepareStarter value(Class<? extends AppPrepareStart> clazz) {
        return value(clazz, LifecycleLevel.CUSTOM_LEVEL_5);
    }

    public static PrepareStarter value(Class<? extends AppPrepareStart> clazz, LifecyclePriority lifeCycleLevel) {
        PrepareStarter lifecycle = getLifecycle(PrepareStarter.class, clazz);
        if (lifecycle == null) {
            lifecycle = new PrepareStarter(clazz, lifeCycleLevel);
            putLifecycle(PrepareStarter.class, lifecycle);
        }
        return lifecycle;
    }

    private PrepareStarter(Class<? extends AppPrepareStart> InitiatorClass, LifecyclePriority lifeCycleLevel) {
        super(PrepareStarter.class, InitiatorClass, lifeCycleLevel);
    }

    @Override
    protected PrepareStarter of(Class<? extends AppPrepareStart> clazz) {
        return value(clazz);
    }

}
