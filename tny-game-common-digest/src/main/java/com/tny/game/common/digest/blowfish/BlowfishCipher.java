/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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