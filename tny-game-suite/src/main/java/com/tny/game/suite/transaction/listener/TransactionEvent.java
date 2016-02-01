package com.tny.game.suite.transaction.listener;

import com.tny.game.common.utils.collection.EnumUtitls;
import com.tny.game.event.Event;
import com.tny.game.suite.transaction.Transaction;

public class TransactionEvent extends Event<Transaction> {


    public static enum TransactionEventType {

        /**
         * 挑战
         */
        OPEN,

        /**
         * 重设
         */
        CLOSE,

        ROLLBACK;

        public final String handle;

        TransactionEventType() {
            this.handle = EnumUtitls.nameFormat(this);
        }

    }

    private TransactionEvent(TransactionEventType handler, Transaction source) {
        super(handler.handle, source);
    }

    public static void dispatch(TransactionEventType handler, Transaction source) {
        new TransactionEvent(handler, source).dispatch();
    }

}
