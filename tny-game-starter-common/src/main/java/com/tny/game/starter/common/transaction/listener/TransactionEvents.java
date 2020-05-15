package com.tny.game.starter.common.transaction.listener;

import com.tny.game.common.event.*;
import com.tny.game.starter.common.transaction.*;

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