package com.tny.game.net.codec.cryptoloy;

import com.tny.game.common.unit.annotation.Unit;
import com.tny.game.common.utils.BytesAide;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.DataPacketV1Config;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 14:58
 */
@Unit
public class XOrCodecCrypto implements CodecCrypto {

    @Override
    public byte[] encrypt(DataPackager packager, byte[] bytes) {
        return xor(packager, bytes);
    }

    @Override
    public byte[] decrypt(DataPackager packager, byte[] bytes) {
        return xor(packager, bytes);
    }

    public byte[] xor(DataPackager packager, byte[] bytes) {
        DataPacketV1Config config = packager.getConfig();
        byte [] security = config.getSecurityKeyBytes(packager.getPacketNumber());
        byte [] code = BytesAide.int2Bytes(packager.getPacketCode());
        return BytesAide.xor(bytes, security, code);
    }


}
