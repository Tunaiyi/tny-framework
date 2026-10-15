/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.result.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/9 20:23
 **/
public interface RpcEnterCompletable {

    /**
     * 静默完成
     *
     * @return 是否完成成功
     */
    default boolean completeSilently() {
        return completeSilently(null);
    }

    /**
     * 静默完成
     *
     * @param error 错误原因
     * @return 是否完成成功
     */
    boolean completeSilently(Throwable error);

    /**
     * 静默完成
     *
     * @param body 错误原因
     * @return 是否完成成功
     */
    boolean completeSilently(ResultCode code, Object body);

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    default boolean complete(ResultCode code) {
        return complete(code, null);
    }

    /**
     * 完成并响应
     *
     * @param content 响应消息
     * @return 是否完成成功
     */
    default boolean complete(MessageContent content) {
        return complete(content, null);
    }

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    default boolean complete(ResultCode code, Object body) {
        return complete(code, body, null);
    }

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    boolean complete(ResultCode code, Throwable error);

    /**
     * 完成并响应
     *
     * @param code 结果码
     * @return 是否完成成功
     */
    boolean complete(ResultCode code, Object body, Throwable error);

    /**
     * 完成并响应
     *
     * @param content 响应消息
     * @param error   错误原因
     * @return 是否完成成功
     */
    boolean complete(MessageContent content, Throwable error);

}
