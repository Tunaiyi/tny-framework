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
