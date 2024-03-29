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
