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

import com.tny.game.common.concurrent.*;
import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface SendReceipt {

    /**
     * @return 获取响应 Future, 如果没有返回 null
     */
    CompletionStageFuture<Message> respond();

    /**
     * @return 是否有响应 Future
     */
    boolean isRespondAwaitable();

    /**
     * @return 获取发送 Future, 如果没有返回 null
     */
    CompletionStageFuture<Void> written();

    /**
     * @return 是否有发送 Future
     */
    boolean isWriteAwaitable();

}
