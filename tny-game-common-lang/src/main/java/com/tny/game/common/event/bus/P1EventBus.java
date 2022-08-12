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
public class P1EventBus<S, A> extends BaseEventBus<P1EventDelegate<S, A>> {

    public P1EventBus(List<P1EventDelegate<S, A>> listeners) {
        super(listeners);
    }

    public void notify(S source, A a) {
        for (P1EventDelegate<S, A> delegate : this.listeners) {
            try {
                delegate.invoke(source, a);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}