package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/30 4:23 下午
 */
public interface GameTaskReceiverFactory {

	GameTaskReceiver create(TaskReceiverType type, long userId);

}
