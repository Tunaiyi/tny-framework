package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.DataPackager;
import com.tny.game.net.codec.v1.DataPacketV1Config;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-17 11:09
 */
public abstract class NettyBytesWaster {

    // 废字节位数
    protected int wasteBitSize;
    // 完全废弃的字节数
    protected int fullWasteByteSize;
    // 需要右移的字节右移的位数
    protected int rightShiftBits;
    // 需要左移的字节左移的位数
    protected int leftShiftBits;
    // 总废字节数
    protected int totalWasteByteSize;

    public NettyBytesWaster(DataPackager packager, boolean waste, DataPacketV1Config config) {
        if (waste) {
            this.wasteBitSize = packager.getPacketCode() % config.getMaxWasteBitSize();
            this.fullWasteByteSize = wasteBitSize / 8;
            this.rightShiftBits = wasteBitSize % 8;
            this.leftShiftBits = 8 - rightShiftBits;
            this.totalWasteByteSize = fullWasteByteSize + (rightShiftBits == 0 ? 0 : 1);          // 总废字节数
        } else {
            this.wasteBitSize = 0;
            this.fullWasteByteSize = 0;
            this.rightShiftBits = 0;
            this.leftShiftBits = 0;
            this.totalWasteByteSize = 0;          // 总废字节数
        }
    }

    public int getWasteBitSize() {
        return wasteBitSize;
    }

    public int getFullWasteByteSize() {
        return fullWasteByteSize;
    }

    public int getRightShiftBits() {
        return rightShiftBits;
    }

    public int getLeftShiftBits() {
        return leftShiftBits;
    }

    public int getTotalWasteByteSize() {
        return totalWasteByteSize;
    }


}
