/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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