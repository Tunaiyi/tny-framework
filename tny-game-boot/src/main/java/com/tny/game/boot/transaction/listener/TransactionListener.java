package com.tny.game.boot.transaction.listener;

import com.tny.game.boot.transaction.*;

public interface TransactionListener {

    void handleOpen(Transaction source);

    void handleClose(Transaction source);

    void handleRollback(Transaction source, Throwable cause);

}
