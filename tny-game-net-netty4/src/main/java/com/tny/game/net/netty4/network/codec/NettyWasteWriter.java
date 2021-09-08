package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-17 11:09
 */
public class NettyWasteWriter extends NettyBytesWaster {

	public NettyWasteWriter(DataPackageContext packager, DataPackCodecOptions config) {
		super(packager, config.isWasteBytesEnable(), config);
	}

	public void write(ByteBuf wasteBuffer, ByteBuf bodyBuffer) {
		for (int index = 0; index < this.fullWasteByteSize; index++) {
			wasteBuffer.writeByte(getWasteByte());
		}
		if (bodyBuffer.isReadable()) {
			if (this.rightShiftBits > 0) {
				byte current = 0;
				byte writeValue = getWasteByte();
				byte prevByte = writeValue;
				int leftShiftBits = 8 - this.rightShiftBits;
				while (bodyBuffer.isReadable()) {
					current = bodyBuffer.readByte();
					writeValue = (byte)((prevByte & 0xff) << leftShiftBits);
					writeValue = (byte)(writeValue | (byte)((current & 0xff) >>> this.rightShiftBits));
					wasteBuffer.writeByte(writeValue);
					prevByte = current;
				}
				// 补最后一个字节
				writeValue = (byte)(((prevByte & 0xff) << leftShiftBits));
				writeValue = (byte)(writeValue | (byte)((getWasteByte() & 0xff) >>> this.rightShiftBits));
				wasteBuffer.writeByte(writeValue);
			} else {
				wasteBuffer.writeBytes(bodyBuffer);
			}
		}
	}

	/**
	 * @return 获取废字节
	 */
	private byte getWasteByte() {
		return (byte)ThreadLocalRandom.current().nextInt();
	}

}
