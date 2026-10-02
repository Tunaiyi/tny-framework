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
package com.tny.game.net.message;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 2:25 下午
 */
public interface MessageSchema extends Protocol {

    /**
     * @return 响应消息 -1 为无
     */
    long getToMessage();

    // /**
    //  * @return 消息类型
    //  */
    // default MessageType getType() {
    //     return getMode().getType();
    // }

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();

}
