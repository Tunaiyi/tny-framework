package com.tny.game.base.exception;

import com.tny.game.base.item.CountableStuff;
import com.tny.game.base.item.ItemModel;
import com.tny.game.common.result.ResultCode;

public class StuffAlertException extends GameRuningException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Number number;

    private Number stuffID;

    private Number alertNumber;

    public StuffAlertException(ResultCode code, long playerID, ItemModel model, Number number, Number alert, Object... messages) {
        super(code, messages);
        this.number = number;
        this.stuffID = model.getID();
        this.alertNumber = alert;
    }

    public StuffAlertException(ResultCode code, CountableStuff<?, ?> stuff, Number alert, Object... messages) {
        this(code, stuff.getPlayerID(), stuff.getModel(), stuff.getNumber(), alert, messages);
    }

    public Number getNumber() {
        return this.number;
    }

    public Number getStuffID() {
        return this.stuffID;
    }

    public Number getAlertNumber() {
        return this.alertNumber;
    }

}
