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
