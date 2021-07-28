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
