package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import io.netty.buffer.ByteBuf;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-17 11:09
 */
public class NettyWasteReader extends NettyBytesWaster {

    public NettyWasteReader(DataPackager packager, boolean waste, DataPacketConfig config) {
        super(packager, waste, config);
    }

    @Override
    protected void setWasteBuffer(ByteBuf byteBuf) {
        wasteBuffer.skipBytes(fullWasteByteSize);
    }

    public byte[] read(int length) {
        byte[] bytes = new byte[length - totalWasteByteSize];
        if (rightShiftBits == 0) {
            wasteBuffer.readBytes(bytes);
        } else {
            int leftShiftBits = 8 - rightShiftBits;
            for (int index = 0; index < bytes.length; index++) {
                byte data = wasteBuffer.readByte();
                if (index == 0) {
                    bytes[index] = (byte) ((data & 0xff) << rightShiftBits);
                } else {
                    byte value = bytes[index - 1];
                    bytes[index - 1] = (byte) (value | (byte) ((data & 0xff) >>> leftShiftBits));
                    bytes[index] = (byte) ((data & 0xff) << rightShiftBits);
                }
            }
            byte data = wasteBuffer.readByte();
            if (bytes.length > 0)
                bytes[bytes.length - 1] = (byte) (bytes[bytes.length - 1] | (byte) ((data & 0xff) >>> leftShiftBits));
        }
        return bytes;
    }

}
