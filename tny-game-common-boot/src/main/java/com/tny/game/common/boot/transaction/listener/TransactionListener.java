package com.tny.game.common.boot.transaction.listener;

import com.tny.game.common.boot.transaction.*;

public interface TransactionListener {

    void handleOpen(Transaction source);

    void handleClose(Transaction source);

    void handleRollback(Transaction source, Throwable cause);

}
