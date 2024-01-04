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

package com.tny.game.basics.item;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.dto.*;
import com.tny.game.basics.utlis.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;

public class CommandResults extends RpcResults {

    public static RpcResult fail(TryToDoResult tryToDoResult) {
        DemandResult result = tryToDoResult.getFailResult();
        DemandType demandType = result.getDemandType();
        ResultCode code = demandType.getResultCode();
        if (code != null) {
            return fail(code, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
        }
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
    }

    public static RpcResult fail(TryResult<?> done) {
        Asserts.checkArgument(done.isFailure(), "TryDone is success");
        if (done.isFailedToTry()) {
            return fail(done.getResult());
        } else {
            return fail(done.getCode());
        }
    }

    public static RpcResult fail(TryToDoException exception) {
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.exception2DTO(exception));
    }

}
