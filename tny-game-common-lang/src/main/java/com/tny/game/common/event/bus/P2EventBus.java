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

package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P2EventBus<S, A1, A2> extends BaseEventBus<P2EventDelegate<S, A1, A2>> {

    public P2EventBus(List<P2EventDelegate<S, A1, A2>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2) {
        for (P2EventDelegate<S, A1, A2> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}