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
package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/11 4:23 下午
 */
public class ByteBufMessageBody implements OctetMessageBody, AutoCloseable {

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * 消息体 buf
     */
    private ByteBuf buffer;

    private final Cleanable cleanable;

    public ByteBufMessageBody(ByteBuf buffer) {
        this.buffer = buffer;
        AtomicBoolean released = new AtomicBoolean(false);
        this.cleanable = cleaner.register(this, () -> doRelease(released, buffer));
    }

    private static void doRelease(AtomicBoolean released, ByteBuf buffer) {
        if (released.compareAndSet(false, true)) {
            if (buffer != null) {
                ReferenceCountUtil.release(buffer);
            }
        }
    }

    @Override
    public ByteBuf getBody() {
        return buffer;
    }

    @Override
    public void release() {
        cleanable.clean();
        this.buffer = null;
    }

    @Override
    public void close() throws Exception {
        release();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ByteBufMessageBody.class.getSimpleName() + "[", "]")
                .add("size=" + buffer.readableBytes())
                .toString();
    }

}
