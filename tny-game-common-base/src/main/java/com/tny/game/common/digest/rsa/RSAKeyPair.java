package com.tny.game.common.digest.rsa;

import java.security.KeyPair;
import java.security.interfaces.*;

public class RSAKeyPair {

    private KeyPair keyPair;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    protected RSAKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public RSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

}
