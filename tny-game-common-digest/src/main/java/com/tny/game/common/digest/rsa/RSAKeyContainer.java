/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
