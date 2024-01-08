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

package com.tny.game.net.application;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.ExecutionException;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface ClientGuide {

    /**
     * @return 是否关闭
     */
    boolean isClosed();

    /**
     * @return 关闭
     */
    boolean close();

    default CompletionStageFuture<NetTunnel> connectAsync(URL url) {
        return connectAsync(url, null);
    }

    CompletionStageFuture<NetTunnel> connectAsync(URL url, TunnelUnavailableWatch watch);

    /**
     * @param url url
     * @return 返回客户端
     */
    NetTunnel connect(URL url, TunnelUnavailableWatch watch) throws ExecutionException, InterruptedException;


    default NetTunnel connect(URL url) throws ExecutionException, InterruptedException {
        return connect(url, null);
    }

    ClientBootstrapSetting getSetting();

}
