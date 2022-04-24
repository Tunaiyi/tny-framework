package com.tny.game.common.digest.rsa;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RSAKeyContainer {

    private List<RSAKeyPair> keyPairs = new ArrayList<>();

    private Map<Object, RSAKeyPair> keyPairsMap = new HashMap<>();

    public RSAKeyContainer(int pairSize) throws Exception {
        this(pairSize, 1024);
    }

    public RSAKeyContainer(int pairSize, int keySize) throws Exception {
        for (int index = 0; index < pairSize; index++) {
            RSAKeyPair pair = RSAUtils.getKeyPair(keySize);
            this.keyPairs.add(pair);
            this.keyPairsMap.put(pair.getPrivateKey(), pair);
            this.keyPairsMap.put(pair.getPublicKey(), pair);
        }
    }

    public RSAKeyPair getKeyPair() {
        int index = ThreadLocalRandom.current().nextInt(this.keyPairs.size());
        return this.keyPairs.get(index);
    }

    public RSAKeyPair getKeyPair(Object key) {
        return this.keyPairsMap.get(key);
    }

}
