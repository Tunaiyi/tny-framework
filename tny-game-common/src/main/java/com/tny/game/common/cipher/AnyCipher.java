package com.tny.game.common.cipher;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-23 03:16
 */
public interface AnyCipher {

    byte[] encrypt(byte[] data) throws Exception;

    byte[] decrypt(byte[] encrypted) throws Exception;

    String encrypt(String data) throws Exception;

    String decrypt(String encrypted) throws Exception;

}
