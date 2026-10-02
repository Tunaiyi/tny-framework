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
