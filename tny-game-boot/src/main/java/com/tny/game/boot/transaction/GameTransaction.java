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

package com.tny.game.boot.transaction;

import com.tny.game.boot.transaction.listener.*;
import com.tny.game.common.context.*;

public class GameTransaction implements Transaction {

    private final Attributes attributes = ContextAttributes.create();

    private boolean working;

    private final Thread thread;

    public GameTransaction() {
        this.thread = Thread.currentThread();
    }

    protected boolean open() {
        if (!this.working) {
            this.working = true;
            this.attributes.clearAttribute();
            TransactionEvents.OPEN_EVENT.notify(this);
            return true;
        }
        return false;
    }

    protected boolean close() {
        if (this.working) {
            this.working = false;
            TransactionEvents.CLOSE_EVENT.notify(this);
            this.attributes.clearAttribute();
            return true;
        }
        return false;
    }

    protected boolean rollback(Throwable cause) {
        if (this.working) {
            this.working = false;
            TransactionEvents.ROLLBACK_EVENT.notify(this, cause);
            this.attributes.clearAttribute();
            return true;
        }
        return false;

    }

    @Override
    public boolean isOpen() {
        return this.working;
    }

    @Override
    public Attributes attributes() {
        return this.attributes;
    }

    @Override
    public String toString() {
        return "GameTransaction{thread=" + this.thread + '}';
    }

}
