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

import com.tny.game.common.type.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageSubject extends MessageHeaderContainer, MessageSchema {

    /**
     * @return 获取结果码
     */
    int getCode();

    /**
     * @return 是否存在消息
     */
    boolean existBody();

    /**
     * @return 获取消息体
     */
    Object getBody();

    /**
     * @return 获取消息体
     */
    <T> T bodyAs(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T bodyAs(ReferenceType<T> clazz);

}
