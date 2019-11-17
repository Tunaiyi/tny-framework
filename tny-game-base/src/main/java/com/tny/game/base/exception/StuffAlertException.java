package com.tny.game.base.exception;

import com.tny.game.base.item.*;
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

    public StuffAlertException(ResultCode code, CountableStuff<?, ?> stuff, Number alert, Object... messages) {
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
