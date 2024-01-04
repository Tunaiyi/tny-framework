/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.digest.blowfish;

import com.tny.game.common.digest.*;

import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.*;

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
        super(secretKey.getBytes(UTF_8), null, DEFAULT_TRANSFORM);
    }

    public BlowfishCipher(String secretKey, String transform) throws Exception {
        super(secretKey.getBytes(UTF_8), null, transform);
    }

    public BlowfishCipher(String secretKey, String ivParameter, String transform) throws Exception {
        super(secretKey.getBytes(UTF_8), ivParameter, transform);
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