/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.buff;

import org.slf4j.*;

import java.nio.ByteBuffer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/12 3:18 下午
 */
public class NioByteBufferAllocator implements ByteBufferAllocator {

    public static final Logger LOGGER = LoggerFactory.getLogger(NioByteBufferAllocator.class);

    private int size = 0;

    private int count = 0;

    @Override
    public ByteBuffer alloc(int capacity) {
        size += capacity;
        count++;
        return ByteBuffer.allocate(capacity);
    }

    @Override
    public void release() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("NioByteBufferAllocator release {} count, total {} size", this.count, this.size);
        }
    }

}
