package com.tny.game.net.base;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.endpoint.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 2:43 下午
 */
public interface ClientConnectFuture<UID> extends CompletionStageFuture<Client<UID>> {

}
