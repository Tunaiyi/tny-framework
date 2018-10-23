package com.tny.game.net.codec.verifier;

import com.tny.game.net.codec.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 14:47
 */
public class NoneCodecVerifier implements CodecVerifier {

    @Override
    public int getCodeLength() {
        return 0;
    }

    @Override
    public byte[] generate(DataPackager packager, byte[] body, long time) {
        return new byte[0];
    }

    @Override
    public boolean verify(DataPackager packager, byte[] body, long time, byte[] verifyCode) {
        return true;
    }

}
