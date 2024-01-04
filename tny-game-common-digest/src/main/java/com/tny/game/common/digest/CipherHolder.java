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

package com.tny.game.common.digest;

import javax.crypto.Cipher;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-21 03:00
 */
class CipherHolder {

    private volatile WeakReference<Cipher> reference;

    private final Callable<Cipher> cipherSupplier;

    public CipherHolder(Callable<Cipher> cipherSupplier) {
        this.cipherSupplier = cipherSupplier;
    }

    public Cipher getCipher() throws Exception {
        if (this.reference != null) {
            Cipher cipher = this.reference.get();
            if (cipher != null) {
                return cipher;
            }
        }
        Cipher cipher = this.cipherSupplier.call();
        this.reference = new WeakReference<>(cipher);
        return cipher;
    }

}
