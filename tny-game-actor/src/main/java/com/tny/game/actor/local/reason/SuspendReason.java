package com.tny.game.actor.local.reason;

/**
 * 挂起Actor原因
 * @author KGTny
 *
 */
public interface SuspendReason {

	/**
	 * @return 挂起Actor原因类型
	 */
	SuspendReasonType getReasonType();

}
