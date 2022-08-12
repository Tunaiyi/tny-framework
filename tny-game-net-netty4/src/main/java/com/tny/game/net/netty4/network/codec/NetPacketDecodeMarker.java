/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network.codec;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 8:22 下午
 */
public class NetPacketDecodeMarker {

    private byte option = 0;

    private int payloadLength = 0;

    private boolean mark = false;

    public NetPacketDecodeMarker() {
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
