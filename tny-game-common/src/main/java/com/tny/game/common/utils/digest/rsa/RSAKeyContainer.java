package com.tny.game.common.utils.digest.rsa;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RSAKeyContainer {

    private List<RSAKeyPair> keyPairs = new ArrayList<RSAKeyPair>();

    private Map<Object, RSAKeyPair> keyPairsMap = new HashMap<Object, RSAKeyPair>();

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
