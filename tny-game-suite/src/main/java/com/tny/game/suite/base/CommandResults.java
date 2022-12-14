package com.tny.game.suite.base;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.utlis.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.command.*;
import com.tny.game.suite.base.dto.*;

public class CommandResults extends com.tny.game.net.base.CommandResults {

    public static CommandResult fail(TryToDoResult tryToDoResult) {
        DemandResult result = tryToDoResult.getFailResult();
        DemandType demandType = result.getDemandType();
        ResultCode code = demandType.getResultCode();
        if (code != null) {
            return fail(code, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
        }
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
    }

    public static CommandResult fail(TryResult<?> done) {
        Asserts.checkArgument(done.isFailure(), "TryDone is success");
        if (done.isFailedToTry()) {
            return fail(done.getResult());
        } else {
            return fail(done.getCode());
        }
    }

    public static CommandResult fail(TryToDoException exception) {
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.exception2DTO(exception));
    }

}
