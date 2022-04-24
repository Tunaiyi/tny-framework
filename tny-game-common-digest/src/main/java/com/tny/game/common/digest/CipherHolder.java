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
