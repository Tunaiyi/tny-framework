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
