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
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PostStarter extends Lifecycle<PostStarter, AppPostStart> {

    public static PostStarter value(Class<? extends AppPostStart> clazz) {
        return value(clazz, LifecycleLevel.CUSTOM_LEVEL_5);
    }

    public static PostStarter value(Class<? extends AppPostStart> clazz, LifecyclePriority lifeCycleLevel) {
        PostStarter lifecycle = getLifecycle(PostStarter.class, clazz);
        if (lifecycle == null) {
            // 并发首注册：putIfAbsent 语义返回唯一实例（原 check-then-act 会丢失注册或抛"已经存在"）
            lifecycle = putIfAbsentLifecycle(PostStarter.class, new PostStarter(clazz, lifeCycleLevel));
        }
        return lifecycle;
    }

    private PostStarter(Class<? extends AppPostStart> InitiatorClass, LifecyclePriority lifeCycleLevel) {
        super(PostStarter.class, InitiatorClass, lifeCycleLevel);
    }

    @Override
    protected PostStarter of(Class<? extends AppPostStart> clazz) {
        return value(clazz);
    }

    @Override
    protected PostStarter of(Class<? extends AppPostStart> clazz, LifecyclePriority priority) {
        return value(clazz, priority);
    }

}
