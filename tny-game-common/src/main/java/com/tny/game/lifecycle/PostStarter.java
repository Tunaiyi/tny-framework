package com.tny.game.lifecycle;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PostStarter extends Lifecycle<PostStarter, ServerPostStart> {

    public static PostStarter value(Class<? extends ServerPostStart> clazz) {
        return value(clazz, LifecycleLevel.LEVEL_5);
    }

    public static PostStarter value(Class<? extends ServerPostStart> clazz, LifecyclePriority lifeCycleLevel) {
        PostStarter lifecycle = getLifecycle(PostStarter.class, clazz);
        if (lifecycle == null) {
            lifecycle = new PostStarter(clazz, lifeCycleLevel);
            putLifecycle(PostStarter.class, lifecycle);
        }
        return lifecycle;
    }

    private PostStarter(Class<? extends ServerPostStart> initerClass, LifecyclePriority lifeCycleLevel) {
        super(initerClass, lifeCycleLevel);
    }

    @Override
    protected PostStarter of(Class<? extends ServerPostStart> clazz) {
        return value(clazz);
    }
}
