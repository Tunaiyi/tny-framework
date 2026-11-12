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
package com.tny.game.common.event;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class ArgsEvent<D, S, A1, A2, A3, A4, A5, E extends ArgsEvent<D, S, A1, A2, A3, A4, A5, E>> extends BaseEvent<D, E> {

    protected ArgsEvent() {
    }

    protected ArgsEvent(E parent) {
        super(parent);
    }

    protected ArgsEvent(Supplier<Collection<D>> factory) {
        super(factory);
    }

    protected final void doNotify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        if (parent != null) {
            doParentNotify(parent, source, a1, a2, a3, a4, a5);
        }
        for (D delegate : this.readOnlyListeners()) {
            try {
                doDelegateNotify(delegate, source, a1, a2, a3, a4, a5);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    public abstract void doParentNotify(E parent, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

    public abstract void doDelegateNotify(D delegate, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
