/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;
import io.netty.buffer.ByteBuf;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-17 11:09
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
                        readValue = (byte) (readValue | (byte) ((currentValue & 0xff) >>> leftShiftBits));
                        bodyBuffer.writeByte(readValue);
                    }
                    readValue = (byte) ((currentValue & 0xff) << this.rightShiftBits);
                }
                currentValue = wasteBuffer.readByte();
                readValue = (byte) (readValue | (byte) ((currentValue & 0xff) >>> leftShiftBits));
                bodyBuffer.writeByte(readValue);
            }
        }
    }

}
