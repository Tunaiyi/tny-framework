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
public class ClientConnectPromise extends CompleteStageFuture<Client> implements ClientConnectFuture {

    public static ClientConnectPromise connected(URL url) {
        ClientConnectPromise future = new ClientConnectPromise();
        future.completeExceptionally(new TunnelConnectException("client {} connected", url));
        return future;
    }

    public static ClientConnectPromise closed(URL url) {
        ClientConnectPromise future = new ClientConnectPromise();
        future.completeExceptionally(new EndpointClosedException("client {} close", url));
        return future;
    }

    public static ClientConnectPromise success(Client client) {
        ClientConnectPromise future = new ClientConnectPromise();
        future.complete(client);
        return future;
    }

}
