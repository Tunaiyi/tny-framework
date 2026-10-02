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
