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

package com.tny.game.common.digest.aes;

import com.tny.game.common.digest.*;

import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-19 04:27
 */
public class AESCipher extends BaseCipher {

    private static final String DEFAULT_TRANSFORM = "AES/CBC/PKCS5Padding";

    private static final String KEY_TYPE = "AES";

    public AESCipher(String secretKey, String ivParameter) throws Exception {
        super(secretKey.getBytes(UTF_8), ivParameter, DEFAULT_TRANSFORM);
    }

    public AESCipher(String secretKey, String ivParameter, String transform) throws Exception {
        super(secretKey.getBytes(UTF_8), ivParameter, transform);
    }

    public AESCipher(byte[] secretKey, String ivParameter) throws Exception {
        super(secretKey, ivParameter, DEFAULT_TRANSFORM);
    }

    public AESCipher(byte[] secretKey, String ivParameter, String transform) throws Exception {
        super(secretKey, ivParameter, transform);
    }

    @Override
    protected SecretKeySpec keyGenerate(byte[] secretKey) throws Exception {
        return new SecretKeySpec(secretKey, KEY_TYPE);
    }

}