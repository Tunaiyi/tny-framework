package com.tny.game.net.codec.cryptoloy;

import com.tny.game.common.utils.BytesAide;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.codec.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 14:58
 */
@Unit("XOrCodecCryptology")
public class XOrCodecCryptology implements CodecCryptology {

    @Override
    public byte[] encrypt(DataPackager packager, byte[] bytes) {
        return xor(packager, bytes);
    }

    @Override
    public byte[] decrypt(DataPackager packager, byte[] bytes) {
        return xor(packager, bytes);
    }

    public byte[] xor(DataPackager packager, byte[] bytes) {
        DataPacketConfig config = packager.getConfig();
        byte [] security = config.getSecurityKey(packager.getPacketNumber());
        byte [] code = BytesAide.int2Bytes(packager.getPacketCode());
        return BytesAide.xor(bytes, security, code);
    }


}
