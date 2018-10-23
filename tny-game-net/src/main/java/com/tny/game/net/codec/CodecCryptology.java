package com.tny.game.net.codec;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018 -10-16 21:06
 */
public interface CodecCryptology {

    byte[] encrypt(DataPackager packager, byte [] bytes);

    byte[] decrypt(DataPackager packager, byte [] bytes);

}
