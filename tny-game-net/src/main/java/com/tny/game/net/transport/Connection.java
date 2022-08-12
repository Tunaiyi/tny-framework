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

import com.tny.game.common.context.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:59 下午
 */
public interface Connection {

    /**
     * @return 远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * @return 是否活跃
     */
    boolean isActive();

    /**
     * @return 是否关闭终端
     */
    boolean isClosed();

    /**
     * 关闭断开连接
     */
    boolean close();

    /**
     * @return 获取会话属性
     */
    Attributes attributes();

}
