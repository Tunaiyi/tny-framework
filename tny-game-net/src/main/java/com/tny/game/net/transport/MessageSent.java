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

package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageSent {

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
