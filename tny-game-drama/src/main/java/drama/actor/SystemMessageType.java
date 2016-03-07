package drama.actor;

import com.tny.game.common.ExceptionUtils;

public enum SystemMessageType {

	/**
	 * 创建
	 */
	CREATE(CreateSysMsg.class),

	/**
	 * 重新创建
	 */
	RECREATE(RecreateSysMsg.class, true),

	/**
	 * 挂起
	 */
	SUSPEND(SuspendSysMsg.class, true),

	/**
	 * 恢复
	 */
	RESUME(ResumeSysMsg.class, true),

	/**
	 * 终止
	 */
	TERMINATE(TerminateSysMsg.class),

	/**
	 * 子接点监护
	 */
	SUPERVISE(SuperviseSysMsg.class),

	/**
	 * 开始监视
	 */
	WATCH(WatchSysMsg.class),

	/**
	 * 取消监视
	 */
	UNWATCH(UnwatchSysMsg.class),

	/**
	 * 无消息
	 */
	NONE(NoneSysMsg.class),

	/**
	 * 处理失败
	 */
	FAILED(FailedSysMsg.class, false, true),

	/**
	 * 监控到死亡事件
	 */
	DEATH_WATCHED(DeathWatchedSysMsg.class);

	private final boolean waitingForChildren;

	private final boolean failed;

	private Class<?> messageClass;

	SystemMessageType(Class<?> messageClass) {
		this(messageClass, false, false);
	}

	SystemMessageType(Class<?> messageClass, boolean waitingForChildren) {
		this(messageClass, waitingForChildren, false);
	}

	SystemMessageType(Class<?> messageClass, boolean waitingForChildren, boolean failed) {
		this.messageClass = ExceptionUtils.checkNotNull(messageClass);
		this.waitingForChildren = waitingForChildren;
		this.failed = failed;
	}

	public boolean isWaitingForChildren() {
		return waitingForChildren;
	}

	public boolean isFailed() {
		return failed;
	}

	@SuppressWarnings("unchecked")
	public <M extends SystemMessage> M as(SystemMessage message) {
		if (messageClass.isInstance(message))
			return (M) message;
		return (M) message;
	}

}
