package com.tny.game.net.rpc;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:11 下午
 */
public class RpcMessageFuture extends WrapperStageFuture<Message> implements RpcFuture<Message> {

	protected RpcMessageFuture(RespondFuture future) {
		super(future);
	}

}