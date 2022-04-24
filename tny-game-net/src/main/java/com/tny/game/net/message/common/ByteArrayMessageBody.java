package com.tny.game.net.message.common;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 16:37
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

}
