package com.tny.game.net.netty4.datagram.codec;

import com.tny.game.net.codec.*;
import io.netty.buffer.ByteBuf;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-17 11:09
 */
public class NettyWasteReader extends NettyBytesWaster {

	public NettyWasteReader(DataPackageContext packager, boolean waste, DataPackCodecOptions options) {
		super(packager, waste, options);
	}

	public void read(ByteBuf wasteBuffer, int length, ByteBuf bodyBuffer) {
		wasteBuffer.skipBytes(this.fullWasteByteSize);
		if (length > 0) {
			if (this.rightShiftBits == 0) {
				wasteBuffer.readBytes(bodyBuffer, length);
			} else {
				int leftShiftBits = 8 - this.rightShiftBits;
				byte currentValue;
				byte readValue = 0;
				for (int index = 0; index < length; index++) {
					currentValue = wasteBuffer.readByte();
					if (index != 0) {
						readValue = (byte)(readValue | (byte)((currentValue & 0xff) >>> leftShiftBits));
						bodyBuffer.writeByte(readValue);
					}
					readValue = (byte)((currentValue & 0xff) << this.rightShiftBits);
				}
				currentValue = wasteBuffer.readByte();
				readValue = (byte)(readValue | (byte)((currentValue & 0xff) >>> leftShiftBits));
				bodyBuffer.writeByte(readValue);
			}
		}
	}

}
