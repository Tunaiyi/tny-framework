/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
            lifecycle = new PostStarter(clazz, lifeCycleLevel);
            putLifecycle(PostStarter.class, lifecycle);
        }
        return lifecycle;
    }

    private PostStarter(Class<? extends AppPostStart> InitiatorClass, LifecyclePriority lifeCycleLevel) {
        super(InitiatorClass, lifeCycleLevel);
    }

    @Override
    protected PostStarter of(Class<? extends AppPostStart> clazz) {
        return value(clazz);
    }

}
