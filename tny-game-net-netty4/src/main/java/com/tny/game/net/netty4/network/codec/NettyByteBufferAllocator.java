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
