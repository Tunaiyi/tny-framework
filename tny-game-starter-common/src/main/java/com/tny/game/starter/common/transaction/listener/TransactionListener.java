package com.tny.game.starter.common.transaction.listener;

import com.tny.game.starter.common.transaction.*;

public interface TransactionListener {

    void handleOpen(Transaction source);

    void handleClose(Transaction source);

    void handleRollback(Transaction source, Throwable cause);

}
