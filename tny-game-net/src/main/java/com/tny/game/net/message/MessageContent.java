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
package com.tny.game.net.message;

import com.tny.game.common.type.*;

import java.util.Map;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageContent extends MessageSchema {

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
     * @return 获取全部 Header
     */
    Map<String, MessageHeader<?>> getAllHeadersMap();

    /**
     * @return 获取消息体
     */
    <T> T bodyAs(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T bodyAs(ReferenceType<T> clazz);

}
