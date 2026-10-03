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
 * 关闭后清理器
 * Created by Kun Yang on 16/7/24.
 */
public final class PostCloser extends Lifecycle<PostCloser, AppClosed> {

    public static PostCloser value(Class<? extends AppClosed> clazz) {
        return value(clazz, LifecycleLevel.CUSTOM_LEVEL_5);
    }

    public static PostCloser value(Class<? extends AppClosed> clazz, LifecyclePriority lifeCycleLevel) {
        PostCloser lifecycle = getLifecycle(PostCloser.class, clazz);
        if (lifecycle == null) {
            // 并发首注册：putIfAbsent 语义返回唯一实例（原 check-then-act 会丢失注册或抛"已经存在"）
            lifecycle = putIfAbsentLifecycle(PostCloser.class, new PostCloser(clazz, lifeCycleLevel));
        }
        return lifecycle;
    }

    private PostCloser(Class<? extends AppClosed> InitiatorClass, LifecyclePriority lifeCycleLevel) {
        super(PostCloser.class, InitiatorClass, lifeCycleLevel);
    }

    @Override
    protected PostCloser of(Class<? extends AppClosed> clazz) {
        return value(clazz);
    }

    @Override
    protected PostCloser of(Class<? extends AppClosed> clazz, LifecyclePriority priority) {
        return value(clazz, priority);
    }

}
