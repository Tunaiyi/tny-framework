/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/11 4:23 下午
 */
public class ByteBufMessageBody implements OctetMessageBody {

    /**
     * 消息体 buf
     */
    private ByteBuf buffer;

    private final AtomicBoolean released = new AtomicBoolean(false);

    public ByteBufMessageBody(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    protected void finalize() throws Throwable {
        this.release();
    }

    @Override
    public ByteBuf getBody() {
        return buffer;
    }

    @Override
    public void release() {
        if (released.compareAndSet(false, true)) {
            ByteBuf buffer = this.buffer;
            if (buffer != null) {
                this.buffer = null;
                ReferenceCountUtil.release(buffer);
            }
        }
    }

}
