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

package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-17 11:09
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

    public NettyBytesWaster(DataPackageContext packager, boolean waste, DataPackCodecOptions options) {
        if (waste) {
            this.wasteBitSize = packager.getPacketCode() % options.getMaxWasteBitSize();
            this.fullWasteByteSize = this.wasteBitSize / 8;
            this.rightShiftBits = this.wasteBitSize % 8;
            this.leftShiftBits = 8 - this.rightShiftBits;
            this.totalWasteByteSize = this.fullWasteByteSize + (this.rightShiftBits == 0 ? 0 : 1);          // 总废字节数
        } else {
            this.wasteBitSize = 0;
            this.fullWasteByteSize = 0;
            this.rightShiftBits = 0;
            this.leftShiftBits = 0;
            this.totalWasteByteSize = 0;          // 总废字节数
        }
    }

    public int getWasteBitSize() {
        return this.wasteBitSize;
    }

    public int getFullWasteByteSize() {
        return this.fullWasteByteSize;
    }

    public int getRightShiftBits() {
        return this.rightShiftBits;
    }

    public int getLeftShiftBits() {
        return this.leftShiftBits;
    }

    public int getTotalWasteByteSize() {
        return this.totalWasteByteSize;
    }

}
