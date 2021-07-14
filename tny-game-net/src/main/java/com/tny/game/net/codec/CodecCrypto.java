package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018 -10-16 21:06
 */
@UnitInterface
public interface CodecCrypto {

    byte[] encrypt(DataPackageContext packager, byte[] bytes);

    byte[] decrypt(DataPackageContext packager, byte[] bytes);

}
