/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

import java.util.List;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public interface NetTerminal extends NetEndpoint, Terminal {

    long getConnectTimeout();

    int getConnectRetryTimes();

    List<Long> getConnectRetryIntervals();

    boolean isAsyncConnect();

    MessageTransporter connect();

    void reconnect();

    void onConnected(NetTunnel tunnel);

}
