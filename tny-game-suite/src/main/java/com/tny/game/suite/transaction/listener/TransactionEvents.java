package com.tny.game.suite.transaction.listener;

import com.tny.game.common.event.BindP1EventBus;
import com.tny.game.common.event.BindVoidEventBus;
import com.tny.game.common.event.EventBuses;
import com.tny.game.suite.transaction.Transaction;

public interface TransactionEvents {

    BindVoidEventBus<TransactionListener, Transaction> OPEN_EVENT =
            EventBuses.of(TransactionListener.class,
                    TransactionListener::handleOpen);

    BindVoidEventBus<TransactionListener, Transaction> CLOSE_EVENT =
            EventBuses.of(TransactionListener.class,
                    TransactionListener::handleClose);

    BindP1EventBus<TransactionListener, Transaction, Throwable> ROLLBACK_EVENT =
            EventBuses.of(TransactionListener.class,
                    TransactionListener::handleRollback);

}
