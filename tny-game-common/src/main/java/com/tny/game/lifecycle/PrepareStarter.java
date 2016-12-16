package com.tny.game.lifecycle;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PrepareStarter extends Lifecycle<PrepareStarter, ServerPrepareStart> {

    public static PrepareStarter value(Class<? extends ServerPrepareStart> clazz) {
        return value(clazz, LifecycleLevel.LEVEL_5);
    }

    public static PrepareStarter value(Class<? extends ServerPrepareStart> clazz, LifecycleLevel lifeCycleLevel) {
        PrepareStarter lifecycle = getLifecycle(PrepareStarter.class, clazz);
        if (lifecycle == null) {
            lifecycle = new PrepareStarter(clazz, lifeCycleLevel);
            putLifecycle(PrepareStarter.class, lifecycle);
        }
        return lifecycle;
    }

    private PrepareStarter(Class<? extends ServerPrepareStart> initerClass, LifecycleLevel lifeCycleLevel) {
        super(initerClass, lifeCycleLevel);
    }

    @Override
    protected PrepareStarter of(Class clazz) {
        return value(clazz);
    }
}
