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

    /**
     * @return 消息类型
     */
    default MessageType getType() {
        return getMode().getType();
    }

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();

}
