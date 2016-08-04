package com.tny.game.suite.transaction;


public class TransactionManager {

    private static final ThreadLocal<GameTransaction> transThreadLocal = new ThreadLocal<>();

    public static void open() {
        GameTransaction transaction = transThreadLocal.get();
        if (transaction == null) {
            transaction = new GameTransaction();
            transThreadLocal.set(transaction);
        }
        if (!transaction.isOpen()) {
            transaction.open();
        }
    }

    public static Transaction getTransaction() {
        return transThreadLocal.get();
    }

    public static Transaction currentTransaction() {
        open();
        return transThreadLocal.get();
    }

    public static void rollback(Throwable cause) {
        GameTransaction transaction = transThreadLocal.get();
        if (transaction != null && transaction.isOpen())
            transaction.rollback(cause);
    }

    public static void close() {
        GameTransaction transaction = transThreadLocal.get();
        if (transaction != null && transaction.isOpen())
            transaction.close();
    }

}
