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
package com.tny.game.common.lifecycle;

/**
 * 启动前准备初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PrepareStarter extends Lifecycle<PrepareStarter, AppPrepareStart> {

    public static PrepareStarter value(Class<? extends AppPrepareStart> clazz) {
        return value(clazz, LifecycleLevel.CUSTOM_LEVEL_5);
    }

    public static PrepareStarter value(Class<? extends AppPrepareStart> clazz, LifecyclePriority lifeCycleLevel) {
        PrepareStarter lifecycle = getLifecycle(PrepareStarter.class, clazz);
        if (lifecycle == null) {
            // 并发首注册：putIfAbsent 语义返回唯一实例（原 check-then-act 会丢失注册或抛"已经存在"）
            lifecycle = putIfAbsentLifecycle(PrepareStarter.class, new PrepareStarter(clazz, lifeCycleLevel));
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

    @Override
    protected PrepareStarter of(Class<? extends AppPrepareStart> clazz, LifecyclePriority priority) {
        return value(clazz, priority);
    }

}
