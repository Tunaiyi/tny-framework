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

package com.tny.game.basics.exception;

import com.tny.game.basics.item.*;
import com.tny.game.common.result.*;

public class StuffAlertException extends GameRuningException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Number number;

    private Number stuffId;

    private Number alertNumber;

    public StuffAlertException(ResultCode code, long playerId, ItemModel model, Number number, Number alert, Object... messages) {
        super(code, messages);
        this.number = number;
        this.stuffId = model.getId();
        this.alertNumber = alert;
    }

    public StuffAlertException(ResultCode code, MultipleStuff<?, ?> stuff, Number alert, Object... messages) {
        this(code, stuff.getPlayerId(), stuff.getModel(), stuff.getNumber(), alert, messages);
    }

    public Number getNumber() {
        return this.number;
    }

    public Number getStuffId() {
        return this.stuffId;
    }

    public Number getAlertNumber() {
        return this.alertNumber;
    }

}
