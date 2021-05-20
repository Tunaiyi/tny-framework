package com.tny.game.net.netty4.codec;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 8:22 下午
 */
public class DataPacketMarker {

    private byte option = 0;

    private int payloadLength = 0;

    private boolean mark = false;

    public DataPacketMarker() {
    }

    public boolean isMark() {
        return this.mark;
    }

    byte getOption() {
        return this.option;
    }

    int getPayloadLength() {
        return this.payloadLength;
    }

    void record(byte option, int payloadLength) {
        this.option = option;
        this.payloadLength = payloadLength;
        this.mark = true;
    }

    void reset() {
        this.option = 0;
        this.payloadLength = 0;
        this.mark = false;
    }

}
