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

import com.tny.game.common.result.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public abstract class RequestContent extends MessageContent {

    public abstract MessageContent withCode(ResultCode code);

    /**
     * @param body 设置 Message Body
     * @return 返回  content 自身
     */
    @Override
    public abstract RequestContent withBody(Object body);

    /**
     * 设置响应等待者
     *
     * @return 请求 content
     */
    public RequestContent willRespondAwaiter() {
        return willRespondAwaiter(MessageRespondAwaiter.DEFAULT_FUTURE_TIMEOUT);
    }

    /**
     * 设置响应等待者
     *
     * @param timeoutMills 超时时间
     * @return 请求 content
     */
    public abstract RequestContent willRespondAwaiter(long timeoutMills);

    public abstract MessageRespondAwaiter getResponseAwaiter();

}