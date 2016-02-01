package com.tny.game.suite.transaction;


import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.suite.transaction.listener.TransactionEvent;
import com.tny.game.suite.transaction.listener.TransactionEvent.TransactionEventType;

public class GameTransaction implements Transaction {

    private Attributes attributes = ContextAttributes.create();

    private boolean working;

    protected boolean open() {
        if (!working) {
            this.working = true;
            TransactionEvent.dispatch(TransactionEventType.OPEN, this);
            return true;
        }
        return false;
    }

    protected boolean close() {
        if (working) {
            this.working = false;
            TransactionEvent.dispatch(TransactionEventType.CLOSE, this);
            this.attributes.clearAttribute();
            return true;
        }
        return false;
    }

    protected boolean rollback() {
        if (working) {
            this.working = false;
            TransactionEvent.dispatch(TransactionEventType.ROLLBACK, this);
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
