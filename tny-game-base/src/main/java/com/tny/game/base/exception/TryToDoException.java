package com.tny.game.base.exception;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.DemandResult;


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
