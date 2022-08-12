/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.boot.transaction;

public class TransactionManager {

    private static final ThreadLocal<GameTransaction> transThreadLocal = ThreadLocal.withInitial(GameTransaction::new);

    public static void open() {
        GameTransaction transaction = transThreadLocal.get();
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
        try {
            GameTransaction transaction = transThreadLocal.get();
            if (transaction != null && transaction.isOpen()) {
                transaction.rollback(cause);
            }
        } finally {
            transThreadLocal.remove();
        }
    }

    public static void close() {
        try {
            GameTransaction transaction = transThreadLocal.get();
            if (transaction != null && transaction.isOpen()) {
                transaction.close();
            }
        } finally {
            transThreadLocal.remove();
        }
    }

}
