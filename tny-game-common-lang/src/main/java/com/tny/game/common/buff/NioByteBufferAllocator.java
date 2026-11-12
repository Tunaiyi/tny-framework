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
