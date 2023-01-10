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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.context.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 16:50
 **/
public interface RpcContext {

    /**
     * @return 获取消息
     */
    MessageSubject getMessageSubject();

    /**
     * @return 获取终端
     */
    <U> Endpoint<U> getEndpoint();

    /**
     * @return 附加属性
     */
    Attributes attributes();

    /**
     * @return 空
     */
    boolean isEmpty();

}
