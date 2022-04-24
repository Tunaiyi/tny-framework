package com.tny.game.net.codec.cryptoloy;

import com.tny.game.common.digest.binary.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.codec.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 14:58
 */
@Unit
public class XOrCodecCrypto implements CodecCrypto {

    private byte[] xor(DataPackageContext context, byte[] bytes, int offset, int length) {
        byte[] security = context.getPackSecurityKey();
        byte[] code = BytesAide.int2Bytes(context.getPacketCode());
        return BytesAide.xor(bytes, offset, length, security, code);
    }

    @Override
    public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

    @Override
    public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
        return xor(context, bytes, offset, length);
    }

}
