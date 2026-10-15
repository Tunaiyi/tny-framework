/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
