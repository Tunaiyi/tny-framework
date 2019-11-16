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
