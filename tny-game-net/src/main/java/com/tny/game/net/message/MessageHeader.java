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

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 消息头部信息
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/29 15:56
 **/
public abstract class MessageHeader<T extends MessageHeader<T>> {

    public abstract String getKey();

    public T getValue() {
        return as(this);
    }

    public abstract boolean isTransitive();

}
