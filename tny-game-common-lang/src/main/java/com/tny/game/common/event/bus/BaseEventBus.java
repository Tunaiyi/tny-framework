/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.event.bus;

import org.slf4j.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BaseEventBus<D> implements EventBus<D> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseEventBus.class);

    protected List<D> listeners;

    BaseEventBus(List<D> listeners) {
        if (listeners == null || listeners.isEmpty()) {
            listeners = new CopyOnWriteArrayList<>();
        }
        this.listeners = new CopyOnWriteArrayList<>(listeners);
    }

    @Override
    public void add(D listener) {
        this.listeners.add(listener);
    }

    @Override
    public void remove(D listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void clear() {
        this.listeners.clear();
    }

}
