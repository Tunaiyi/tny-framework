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
package com.tny.game.net.message.common;

import java.util.StringJoiner;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-18 16:37
 */
public class ByteArrayMessageBody implements OctetMessageBody {

    /**
     * 消息体字节
     */
    private byte[] bodyBytes;

    public ByteArrayMessageBody(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    @Override
    public byte[] getBody() {
        return bodyBytes;
    }

    @Override
    public void release() {
        bodyBytes = null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ByteArrayMessageBody.class.getSimpleName() + "[", "]")
                .add("size=" + bodyBytes.length)
                .toString();
    }

}
