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

package com.tny.game.net.codec;

import org.slf4j.Logger;

import java.nio.ByteBuffer;

import static com.tny.game.common.digest.binary.BytesAide.*;

public class CodecLogger {

    /**
     * 记录字节二进制
     *
     * @param message  消息
     * @param binaries 字节数据
     */
    public static void logBinary(Logger logger, String message, byte[]... binaries) {
        if (logger.isInfoEnabled()) {
            StringBuilder bodyBinary = new StringBuilder();
            for (byte[] binary : binaries) {
                for (byte data : binary)
                    bodyBinary.append(toBinaryString(data)).append(" ");
            }
            logger.info(message, bodyBinary.toString());
        }
    }

    /**
     * 记录字节二进制
     *
     * @param message  消息
     * @param binaries 字节数据
     */
    public static void logBinary(Logger logger, String message, byte[] binaries, int offset, int length) {
        if (logger.isInfoEnabled()) {
            StringBuilder bodyBinary = new StringBuilder();
            for (int index = 0; index < length; index++) {
                bodyBinary.append(toBinaryString(binaries[offset + index])).append(" ");
            }
            logger.info(message, bodyBinary);
        }
    }

    /**
     * 记录字节二进制
     *
     * @param message  消息
     * @param binaries 字节数据
     */
    public static void logBinary(Logger logger, String message, byte[] binaries) {
        logBinary(logger, message, binaries, 0, binaries.length);
    }

    /**
     * 记录字节二进制
     *
     * @param message    消息
     * @param byteBuffer 缓存
     * @param start      开始位置
     * @param length     长度
     */
    public static void logBinary(Logger logger, String message, ByteBuffer byteBuffer, int start, int length) {
        if (logger.isInfoEnabled()) {
            byteBuffer.position(start);
            byte[] binary = new byte[length];
            byteBuffer.get(binary);
            CodecLogger.logBinary(logger, message, binary);
        }
    }

}
