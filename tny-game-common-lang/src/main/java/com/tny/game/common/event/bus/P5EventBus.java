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
public class P5EventBus<S, A1, A2, A3, A4, A5> extends BaseEventBus<P5EventDelegate<S, A1, A2, A3, A4, A5>> {

    public P5EventBus(List<P5EventDelegate<S, A1, A2, A3, A4, A5>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        for (P5EventDelegate<S, A1, A2, A3, A4, A5> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2, a3, a4, a5);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}