/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.cache;

import com.tny.game.data.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/15 8:32 上午
 */
public class EntityKeyMakerIdConverter<K extends Comparable<?>, O> implements EntityIdConverter<K, O, K> {

    private final EntityKeyMaker<K, O> keyMaker;

    public static <K extends Comparable<?>, O> EntityIdConverter<K, O, K> wrapper(EntityKeyMaker<K, O> keyMaker) {
        return new EntityKeyMakerIdConverter<>(keyMaker);
    }

    private EntityKeyMakerIdConverter(EntityKeyMaker<K, O> keyMaker) {
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
