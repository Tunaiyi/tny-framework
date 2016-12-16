package com.tny.game.lifecycle;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PostCloser extends Lifecycle<PostCloser, ServerPostClose> {

    public static PostCloser value(Class<? extends ServerPostClose> clazz) {
        return value(clazz, LifecycleLevel.LEVEL_5);
    }

    public static PostCloser value(Class<? extends ServerPostClose> clazz, LifecycleLevel lifeCycleLevel) {
        PostCloser lifecycle = getLifecycle(PostCloser.class, clazz);
        if (lifecycle == null) {
            lifecycle = new PostCloser(clazz, lifeCycleLevel);
            putLifecycle(PostCloser.class, lifecycle);
        }
        return lifecycle;
    }

    private PostCloser(Class<? extends ServerPostClose> initerClass, LifecycleLevel lifeCycleLevel) {
        super(initerClass, lifeCycleLevel);
    }

    @Override
    protected PostCloser of(Class<? extends ServerPostClose> clazz) {
        return null;
    }

}
