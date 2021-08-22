package com.tny.game.common.lifecycle;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PrepareStarter extends Lifecycle<PrepareStarter, AppPrepareStart> {

	public static PrepareStarter value(Class<? extends AppPrepareStart> clazz) {
		return value(clazz, LifecycleLevel.CUSTOM_LEVEL_5);
	}

	public static PrepareStarter value(Class<? extends AppPrepareStart> clazz, LifecyclePriority lifeCycleLevel) {
		PrepareStarter lifecycle = getLifecycle(PrepareStarter.class, clazz);
		if (lifecycle == null) {
			lifecycle = new PrepareStarter(clazz, lifeCycleLevel);
			putLifecycle(PrepareStarter.class, lifecycle);
		}
		return lifecycle;
	}

	private PrepareStarter(Class<? extends AppPrepareStart> InitiatorClass, LifecyclePriority lifeCycleLevel) {
		super(InitiatorClass, lifeCycleLevel);
	}

	@Override
	protected PrepareStarter of(Class<? extends AppPrepareStart> clazz) {
		return value(clazz);
	}

}
