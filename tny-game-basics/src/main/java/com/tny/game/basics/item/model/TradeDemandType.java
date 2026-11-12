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

package com.tny.game.basics.item.model;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/16 01:31
 **/
public enum TradeDemandType implements DemandType {

    DEDUCT_DEMAND_GE(1) {
        @Override
        public ResultCode getResultCode() {
            return ItemResultCode.TRY_TO_DO_FAIL;
        }

        @Override
        public boolean isCost() {
            return true;
        }

    };

    private final int id;

    TradeDemandType(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return this.id;
    }

}
