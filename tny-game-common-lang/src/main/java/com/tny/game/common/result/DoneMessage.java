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

package com.tny.game.common.result;

/**
 * Done 的消息载体
 * <p>
 *
 * @author Kun Yang
 */
public interface DoneMessage<M, D extends Done<M>> {

    /**
     * 以message为模板设置消息内容
     *
     * @param params 消息参数
     * @return 返回 DoneResult<M>
     */
    D withMessageParams(Object... params);

    /**
     * 设置消息
     *
     * @param message 消息模板
     * @param params  消息参数
     * @return 返回 DoneResult<M>
     */
    D withMessage(String message, Object... params);

}
