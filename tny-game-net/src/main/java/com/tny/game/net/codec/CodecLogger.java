package com.tny.game.net.codec;

import org.slf4j.Logger;

import java.nio.ByteBuffer;

import static com.tny.game.common.utils.BytesAide.*;

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