/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
