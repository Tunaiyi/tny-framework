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
package com.tny.game.net.rpc;

import com.tny.game.net.session.*;

/**
 * Rpc远程接入点(链接)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:20
 **/
public interface RpcAccess {

    /**
     * @return 访问点 id
     */
    long getAccessId();

    /**
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isActive();

    /**
     * @return session
     */
    Session getSession();
}
