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
