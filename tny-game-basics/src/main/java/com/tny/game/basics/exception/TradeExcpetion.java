/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.exception;

import com.tny.game.common.result.*;

/**
 * 交易异常
 *
 * @author KGTny
 */
public class TradeExcpetion extends GameException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int number;

    private long stuffId;

    private int alertNumber;

    public TradeExcpetion(ResultCode resultCode, long stuffId, int number, int alert, Object... messages) {
        super(resultCode.getCode(), resultCode, format(stuffId, resultCode, messages));
        this.number = number;
        this.stuffId = stuffId;
        this.alertNumber = alert;
    }

    public int getNumber() {
        return number;
    }

    public long getStuffId() {
        return stuffId;
    }

    public int getAlertNumber() {
        return alertNumber;
    }

}
