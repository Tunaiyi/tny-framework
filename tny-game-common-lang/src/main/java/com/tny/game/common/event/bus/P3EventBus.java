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

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P3EventBus<S, A1, A2, A3> extends BaseEventBus<P3EventDelegate<S, A1, A2, A3>> {

    public P3EventBus(List<P3EventDelegate<S, A1, A2, A3>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3) {
        for (P3EventDelegate<S, A1, A2, A3> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2, a3);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}