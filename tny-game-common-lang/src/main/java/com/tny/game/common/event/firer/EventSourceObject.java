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

package com.tny.game.common.event.firer;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:42 下午
 */
public interface EventSourceObject<L> extends EventSource<L> {

    EventSource<L> event();

    @Override
    default void add(L listener) {
        event().add(listener);
    }

    @Override
    default void remove(L listener) {
        event().remove(listener);
    }

    @Override
    default void addListener(Collection<? extends L> listeners) {
        event().addListener(listeners);
    }

    @Override
    default void removeListener(Collection<? extends L> listeners) {
        event().removeListener(listeners);
    }

    @Override
    default void clear() {
        event().clear();
    }

}
