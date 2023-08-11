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
package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:59 下午
 */
public interface Connection extends AddressPeer {

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

}
