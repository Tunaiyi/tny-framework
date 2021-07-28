package com.tny.game.boot.transaction.listener;

import com.tny.game.boot.transaction.*;
import com.tny.game.common.event.bus.*;

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
