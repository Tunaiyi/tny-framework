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
 * Rpc转发HeaderBuilder
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 02:37
 **/
public abstract class MessageHeaderBuilder<H extends MessageHeader<?>> {

    private H header;

    public MessageHeaderBuilder() {
    }

    public boolean isHasHeader() {
        return header != null;
    }

    protected H header() {
        if (header == null) {
            header = create();
        }
        return header;
    }

    protected abstract H create();

    public H build() {
        H header = this.header;
        this.header = null;
        return header;
    }

}
