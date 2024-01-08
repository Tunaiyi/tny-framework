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

package com.tny.game.common.event;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/2/4.
 */
public interface EventWatch<D> {

    void add(D delegate);

    void remove(D delegate);

    void clear();

    default void add(Collection<? extends D> listeners) {
        listeners.forEach(this::add);
    }

    default void remove(Collection<? extends D> listeners) {
        listeners.forEach(this::remove);
    }
}

