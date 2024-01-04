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
