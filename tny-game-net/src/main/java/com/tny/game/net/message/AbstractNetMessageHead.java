/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public abstract class AbstractNetMessageHead implements NetMessageHead {

    protected volatile MessageMode mode;

    protected AbstractNetMessageHead() {
    }

    protected AbstractNetMessageHead(MessageMode mode) {
        this.mode = mode;
    }

    @Override
    public MessageMode getMode() {
        return this.mode;
    }

}
