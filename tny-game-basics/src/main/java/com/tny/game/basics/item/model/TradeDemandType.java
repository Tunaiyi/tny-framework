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
