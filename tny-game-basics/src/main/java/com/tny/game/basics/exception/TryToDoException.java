package com.tny.game.basics.exception;

import com.tny.game.basics.item.behavior.*;

public class TryToDoException extends GameException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Action action;

    private DemandResult demandResult;

    public TryToDoException(Action action, DemandResult data, Object... messages) {
        super(data, ItemResultCode.TRY_TO_DO_FAIL, messages);
        this.action = action;
        this.demandResult = data;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the demandResult
     */
    public DemandResult getDemandResult() {
        return demandResult;
    }

}
