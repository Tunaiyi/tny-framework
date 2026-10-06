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

import com.tny.game.common.buff.*;
import io.netty.buffer.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/13 11:26 上午
 */
public class NettyByteBufferAllocator implements ByteBufferAllocator {

    private final ByteBufAllocator allocator;

    private final List<ByteBuf> byteBufList = new ArrayList<>();

    public NettyByteBufferAllocator() {
        this.allocator = ByteBufAllocator.DEFAULT;
    }

    public NettyByteBufferAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    @Override
    public ByteBuffer alloc(int capacity) {
        ByteBuf byteBuf = allocator.heapBuffer(capacity);
        byteBufList.add(byteBuf);
        return byteBuf.nioBuffer(0, capacity);
    }

    @Override
    public void release() {
        for (ByteBuf byteBuf : byteBufList) {
            ReferenceCountUtil.release(byteBuf);
        }
        byteBufList.clear();
    }

}
