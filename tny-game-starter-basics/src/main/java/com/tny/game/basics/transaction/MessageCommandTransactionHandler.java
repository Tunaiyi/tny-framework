package com.tny.game.basics.transaction;

import com.tny.game.boot.transaction.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.listener.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/24 3:03 下午
 */
@Unit
public class MessageCommandTransactionHandler implements MessageCommandListener {

	public static final Logger LOGGER = LoggerFactory.getLogger(MessageCommandTransactionHandler.class);

	@Override
	public void onExecuteStart(MessageCommand<? extends MessageCommandContext> context) {
		TransactionManager.open();
	}

	@Override
	public void onExecuteEnd(MessageCommand<? extends MessageCommandContext> context, Throwable cause) {
		try {
			TransactionManager.close();
		} catch (Throwable e) {
			try {
				TransactionManager.rollback(e);
			} catch (Throwable ex) {
				LOGGER.warn("协议[{}] => 异常", context.getMessage().getProtocolId(), ex);
			}
		}
	}

}
