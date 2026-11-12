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
package com.tny.game.data.cache;

import com.tny.game.data.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/15 8:32 上午
 */
public class CacheKeyMakerIdConverter<K extends Comparable<?>, O> implements EntityIdConverter<K, O, K> {

    private final CacheKeyMaker<K, O> keyMaker;

    public static <K extends Comparable<?>, O> EntityIdConverter<K, O, K> wrapper(CacheKeyMaker<K, O> keyMaker) {
        return new CacheKeyMakerIdConverter<>(keyMaker);
    }

    private CacheKeyMakerIdConverter(CacheKeyMaker<K, O> keyMaker) {
        this.keyMaker = keyMaker;
    }

    @Override
    public K keyToId(K key) {
        return key;
    }

    @Override
    public K entityToId(O object) {
        return keyMaker.make(object);
    }

}
