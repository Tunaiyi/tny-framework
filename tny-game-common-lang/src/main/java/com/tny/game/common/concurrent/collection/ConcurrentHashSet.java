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

package com.tny.game.common.concurrent.collection;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends MapBackedSet<E> {

    private static final long serialVersionUID = 1L;

    public ConcurrentHashSet() {
        super(new ConcurrentHashMap<>());
    }

    public ConcurrentHashSet(Collection<E> c) {
        super(new ConcurrentHashMap<>(), c);
    }

    @Override
    public boolean add(E o) {
        Boolean answer = this.map.putIfAbsent(o, Boolean.TRUE);
        return answer == null;
    }

}
