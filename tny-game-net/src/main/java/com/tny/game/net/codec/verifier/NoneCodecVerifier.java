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
    public byte[] generate(DataPackageContext packager, byte[] body) {
        return new byte[0];
    }

    @Override
    public boolean verify(DataPackageContext packager, byte[] body, byte[] verifyCode) {
        return true;
    }

}
