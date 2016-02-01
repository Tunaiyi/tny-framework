package com.tny.game.base.exception;

import com.tny.game.common.result.ResultCode;

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

    private long stuffID;

    private int alertNumber;

    public TradeExcpetion(ResultCode resultCode, long stuffID, int number, int alert, Object... messages) {
        super(resultCode.getCode(), resultCode, format(stuffID, resultCode, messages));
        this.number = number;
        this.stuffID = stuffID;
        this.alertNumber = alert;
    }

    public int getNumber() {
        return number;
    }

    public long getStuffID() {
        return stuffID;
    }

    public int getAlertNumber() {
        return alertNumber;
    }
}
