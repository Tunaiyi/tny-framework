package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-17 11:09
 */
public class NettyWasteWriter extends NettyBytesWaster {

    public NettyWasteWriter(DataPackager packager, DataPacketV1Config config) {
        super(packager, config.isWasteBytesEnable(), config);
    }


    public void write(ByteBuf wasteBuffer, byte[] data) {
        for (int index = 0; index < fullWasteByteSize; index++) {
            wasteBuffer.writeByte(getWasteByte());
        }
        if (rightShiftBits > 0) {
            byte firstByte = getWasteByte();
            firstByte = (byte) ((firstByte & 0xff) << leftShiftBits);
            wasteBuffer.writeByte(firstByte);
            int leftShiftBits = 8 - rightShiftBits;
            for (int index = 0; index < data.length; index++) {
                if (index == 0) {
                    firstByte = (byte) ((firstByte & 0xff) << leftShiftBits);
                    firstByte = (byte) (firstByte | (byte) ((data[index] & 0xff) >>> rightShiftBits));
                    wasteBuffer.setByte(wasteBuffer.writerIndex() - 1, firstByte);
                } else {
                    byte value = data[index - 1];
                    value = (byte) ((value & 0xff) << leftShiftBits);
                    value = (byte) (value | (byte) ((data[index] & 0xff) >>> rightShiftBits));
                    wasteBuffer.writeByte(value);
                }
            }
            byte value = 0;
            if (data.length > 0)
                value = (byte) (((data[data.length - 1] & 0xff) << leftShiftBits));
            byte compliant = getWasteByte();
            value = (byte) (value | (byte) ((compliant & 0xff) >>> rightShiftBits));
            wasteBuffer.writeByte(value);
        } else {
            wasteBuffer.writeBytes(data);
        }
    }

    /**
     * @return 获取废字节
     */
    private byte getWasteByte() {
        return (byte) ThreadLocalRandom.current().nextInt();
    }

}
