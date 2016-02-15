package com.tny.game.suite.transaction;


import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.suite.transaction.listener.TransactionEvents;

public class GameTransaction implements Transaction {

    private Attributes attributes = ContextAttributes.create();

    private boolean working;

    protected boolean open() {
        if (!working) {
            this.working = true;
            TransactionEvents.OPEN_EVENT.notify(this);
            return true;
        }
        return false;
    }

    protected boolean close() {
        if (working) {
            this.working = false;
            TransactionEvents.CLOSE_EVENT.notify(this);
            this.attributes.clearAttribute();
            return true;
        }
        return false;
    }

    protected boolean rollback(Throwable cause) {
        if (working) {
            this.working = false;
            TransactionEvents.ROLLBACK_EVENT.notify(this, cause);
            this.attributes.clearAttribute();
            return true;
        }
        return false;

    }

    @Override
    public boolean isOpen() {
        return working;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

}
