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
