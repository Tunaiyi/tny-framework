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
public final class PostCloser extends Lifecycle<PostCloser, AppClosed> {

    public static PostCloser value(Class<? extends AppClosed> clazz) {
        return value(clazz, LifecycleLevel.CUSTOM_LEVEL_5);
    }

    public static PostCloser value(Class<? extends AppClosed> clazz, LifecyclePriority lifeCycleLevel) {
        PostCloser lifecycle = getLifecycle(PostCloser.class, clazz);
        if (lifecycle == null) {
            lifecycle = new PostCloser(clazz, lifeCycleLevel);
            putLifecycle(PostCloser.class, lifecycle);
        }
        return lifecycle;
    }

    private PostCloser(Class<? extends AppClosed> InitiatorClass, LifecyclePriority lifeCycleLevel) {
        super(InitiatorClass, lifeCycleLevel);
    }

    @Override
    protected PostCloser of(Class<? extends AppClosed> clazz) {
        return null;
    }

}
