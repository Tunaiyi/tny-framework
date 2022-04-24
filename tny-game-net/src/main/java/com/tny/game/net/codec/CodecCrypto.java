package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018 -10-16 21:06
 */
@UnitInterface
public interface CodecCrypto {

    byte[] encrypt(DataPackageContext packager, byte[] bytes, int offset, int length);

    byte[] decrypt(DataPackageContext packager, byte[] bytes, int offset, int length);

}
