package com.tny.game.suite.transaction.listener;


import com.tny.game.suite.transaction.Transaction;

public interface TransactionListener {

    void handleOpen(Transaction source);

    void handleClose(Transaction source);

    void handleRollback(Transaction source, Throwable cause);

}
