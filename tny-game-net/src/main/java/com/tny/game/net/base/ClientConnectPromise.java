/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
        future.completeExceptionally(new TunnelConnectException("client {} connected", url));
        return future;
    }

    public static <UID> ClientConnectPromise<UID> closed(URL url) {
        ClientConnectPromise<UID> future = new ClientConnectPromise<>();
        future.completeExceptionally(new EndpointClosedException("client {} close", url));
        return future;
    }

    public static <UID> ClientConnectPromise<UID> success(Client<UID> client) {
        ClientConnectPromise<UID> future = new ClientConnectPromise<>();
        future.complete(client);
        return future;
    }

}
