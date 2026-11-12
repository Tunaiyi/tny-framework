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
 * <p>
 *
 * @author Kun Yang
 * @date 2019-03-18 10:50
 */
public interface MessageTail {

    /**
     * 判断消息是否有头部
     *
     * @return 如果有返回 true, 否则返回 false
     */
    boolean isHasAttachment();

    /**
     * @return 获取消息头
     */
    <T> T getAttachment(Class<T> clazz);

    /**
     * @return 获取消息头
     */
    <T> T getAttachment(ReferenceType<T> clazz);

}
