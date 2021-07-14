package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import io.netty.buffer.ByteBuf;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-17 11:09
 */
public class NettyWasteReader extends NettyBytesWaster {

    public NettyWasteReader(DataPackageContext packager, boolean waste, DataPacketV1Config config) {
        super(packager, waste, config);
    }

    public byte[] read(ByteBuf wasteBuffer, int length) {
        wasteBuffer.skipBytes(this.fullWasteByteSize);
        byte[] bytes = new byte[length - this.totalWasteByteSize];
        if (this.rightShiftBits == 0) {
            wasteBuffer.readBytes(bytes);
        } else {
            int leftShiftBits = 8 - this.rightShiftBits;
            for (int index = 0; index < bytes.length; index++) {
                byte data = wasteBuffer.readByte();
                if (index == 0) {
                    bytes[index] = (byte)((data & 0xff) << this.rightShiftBits);
                } else {
                    byte value = bytes[index - 1];
                    bytes[index - 1] = (byte)(value | (byte)((data & 0xff) >>> leftShiftBits));
                    bytes[index] = (byte)((data & 0xff) << this.rightShiftBits);
                }
            }
            byte data = wasteBuffer.readByte();
            if (bytes.length > 0) {
                bytes[bytes.length - 1] = (byte)(bytes[bytes.length - 1] | (byte)((data & 0xff) >>> leftShiftBits));
            }
        }
        return bytes;
    }

}
