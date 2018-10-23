package com.tny.game.net.codec.cryptoloy;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.codec.*;

/**
 * 无加密加密器
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 14:44
 */
@Unit("NoneCodecCryptology")
public class NoneCodecCryptology implements CodecCryptology {

    @Override
    public byte[] encrypt(DataPackager packager, byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decrypt(DataPackager packager, byte[] bytes) {
        return bytes;
    }

}
