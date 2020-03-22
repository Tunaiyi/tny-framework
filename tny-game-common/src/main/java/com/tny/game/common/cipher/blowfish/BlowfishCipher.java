package com.tny.game.common.cipher.blowfish;

import com.tny.game.common.cipher.*;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-19 04:27
 */
public class BlowfishCipher extends BaseCipher {

    private static final String DEFAULT_TRANSFORM = "Blowfish/ECB/PKCS5Padding";

    private static final String KEY_TYPE = "Blowfish";

    public BlowfishCipher(String secretKey) throws Exception {
        super(Base64.getUrlDecoder().decode(secretKey), null, DEFAULT_TRANSFORM);
    }

    public BlowfishCipher(String secretKey, String transform) throws Exception {
        super(Base64.getUrlDecoder().decode(secretKey), null, transform);
    }

    public BlowfishCipher(String secretKey, String ivParameter, String transform) throws Exception {
        super(Base64.getUrlDecoder().decode(secretKey), ivParameter, transform);
    }

    public BlowfishCipher(byte[] secretKey) throws Exception {
        super(secretKey, null, DEFAULT_TRANSFORM);
    }

    public BlowfishCipher(byte[] secretKey, String transform) throws Exception {
        super(secretKey, null, transform);
    }

    public BlowfishCipher(byte[] secretKey, String ivParameter, String transform) throws Exception {
        super(secretKey, ivParameter, transform);
    }

    @Override
    protected SecretKeySpec keyGenerate(byte[] secretKey) throws Exception {
        return new SecretKeySpec(secretKey, KEY_TYPE);
    }

}