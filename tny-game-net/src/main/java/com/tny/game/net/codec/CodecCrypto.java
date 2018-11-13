package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.UnitInterface;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018 -10-16 21:06
 */
@UnitInterface
public interface CodecCrypto {

    byte[] encrypt(DataPackager packager, byte [] bytes);

    byte[] decrypt(DataPackager packager, byte [] bytes);

}
