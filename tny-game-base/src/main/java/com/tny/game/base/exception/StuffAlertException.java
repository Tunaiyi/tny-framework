package com.tny.game.base.exception;

import com.tny.game.base.item.CountableStuff;
import com.tny.game.base.item.ItemModel;
import com.tny.game.common.result.ResultCode;

public class StuffAlertException extends GameRuningException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long number;

    private long stuffID;

    private long alertNumber;

    public StuffAlertException(ResultCode code, long playerID, ItemModel model, long number, long alert, Object... messages) {
        super(code, messages);
        this.number = number;
        this.stuffID = model.getID();
        this.alertNumber = alert;
    }

    public StuffAlertException(ResultCode code, CountableStuff<?> stuff, long alert, Object... messages) {
        this(code, stuff.getPlayerID(), stuff.getModel(), stuff.getNumber(), alert, messages);
    }

    public long getNumber() {
        return this.number;
    }

    public long getStuffID() {
        return this.stuffID;
    }

    public long getAlertNumber() {
        return this.alertNumber;
    }

}
