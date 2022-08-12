/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.digest;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.*;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-23 03:16
 */
public abstract class BaseCipher implements AnyCipher {

    private ThreadLocal<CipherHolder> encryptCipher;

    private ThreadLocal<CipherHolder> decryptCipher;

    private SecretKeySpec secretKeySpec;

    private IvParameterSpec ivParameterSpec;

    private String transform;

    public BaseCipher(byte[] secretKey, String transform) throws Exception {
        this(secretKey, null, transform);
    }

    public BaseCipher(byte[] secretKey, String ivParameter, String transform) throws Exception {
        this.secretKeySpec = keyGenerate(secretKey);
        this.transform = transform;
        if (StringUtils.isNotBlank(ivParameter)) {
            this.ivParameterSpec = new IvParameterSpec(ivParameter.getBytes());
        }
        this.init();
    }

    private void init() throws Exception {
        this.createCipher(Cipher.ENCRYPT_MODE, this.transform);
        this.createCipher(Cipher.DECRYPT_MODE, this.transform);
        this.encryptCipher = ThreadLocal.withInitial(() ->
                new CipherHolder(() -> this.createCipher(Cipher.ENCRYPT_MODE, this.transform)));
        this.decryptCipher = ThreadLocal.withInitial(() ->
                new CipherHolder(() -> this.createCipher(Cipher.DECRYPT_MODE, this.transform)));
    }

    protected abstract SecretKeySpec keyGenerate(byte[] secretKey) throws Exception;

    protected Cipher createCipher(int mode, String transform) throws Exception {
        Cipher cipher = Cipher.getInstance(transform);
        if (this.ivParameterSpec == null) {
            cipher.init(mode, this.secretKeySpec);
        } else {
            cipher.init(mode, this.secretKeySpec, this.ivParameterSpec);
        }
        return cipher;
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = this.encryptCipher.get().getCipher();
        return cipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] encrypted) throws Exception {
        Cipher cipher = this.decryptCipher.get().getCipher();
        return cipher.doFinal(encrypted);
    }

    @Override
    public String encrypt(String data) throws Exception {
        Cipher cipher = this.encryptCipher.get().getCipher();
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String encrypted) throws Exception {
        Cipher cipher = this.decryptCipher.get().getCipher();
        byte[] original = cipher.doFinal(Base64.getUrlDecoder().decode(encrypted));
        return new String(original, UTF_8);
    }

}
