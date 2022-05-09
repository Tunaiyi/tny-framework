package com.tny.game.basics.item;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.dto.*;
import com.tny.game.basics.utlis.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;

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
