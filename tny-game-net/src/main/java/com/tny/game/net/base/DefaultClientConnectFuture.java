package com.tny.game.net.base;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 2:43 下午
 */
public class DefaultClientConnectFuture<UID> extends CompleteStageFuture<Client<UID>> implements ClientConnectFuture<UID> {

    public static <UID> DefaultClientConnectFuture<UID> connected(URL url) {
        DefaultClientConnectFuture<UID> future = new DefaultClientConnectFuture<>();
        future.completeExceptionally(new ConnectedException("client {} connected", url));
        return future;
    }

    public static <UID> DefaultClientConnectFuture<UID> closed(URL url) {
        DefaultClientConnectFuture<UID> future = new DefaultClientConnectFuture<>();
        future.completeExceptionally(new CloseException("client {} close", url));
        return future;
    }

    public static <UID> DefaultClientConnectFuture<UID> success(Client<UID> client) {
        DefaultClientConnectFuture<UID> future = new DefaultClientConnectFuture<>();
        future.complete(client);
        return future;
    }

}
