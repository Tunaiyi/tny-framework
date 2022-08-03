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
public class ClientConnectPromise<UID> extends CompleteStageFuture<Client<UID>> implements ClientConnectFuture<UID> {

    public static <UID> ClientConnectPromise<UID> connected(URL url) {
        ClientConnectPromise<UID> future = new ClientConnectPromise<>();
        future.completeExceptionally(new ConnectedException("client {} connected", url));
        return future;
    }

    public static <UID> ClientConnectPromise<UID> closed(URL url) {
        ClientConnectPromise<UID> future = new ClientConnectPromise<>();
        future.completeExceptionally(new CloseException("client {} close", url));
        return future;
    }

    public static <UID> ClientConnectPromise<UID> success(Client<UID> client) {
        ClientConnectPromise<UID> future = new ClientConnectPromise<>();
        future.complete(client);
        return future;
    }

}
