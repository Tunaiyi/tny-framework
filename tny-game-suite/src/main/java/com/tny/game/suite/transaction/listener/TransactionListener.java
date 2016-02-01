package com.tny.game.suite.transaction.listener;

public interface TransactionListener {

    void handleOpen(TransactionEvent event);

    void handleClose(TransactionEvent event);

    void handleRollback(TransactionEvent event);

}
