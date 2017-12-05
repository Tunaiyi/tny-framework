package com.tny.game.common.utils;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.exception.TryToDoException;
import com.tny.game.base.item.behavior.DemandResult;
import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.base.utlis.TryResult;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.dto.TryToDoFailDTO;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.command.CommandResult;

public class CommandResults extends ResultFactory {

    public static CommandResult fail(TryToDoResult tryToDoResult) {
        DemandResult result = tryToDoResult.getFailResult();
        DemandType demandType = result.getDemandType();
        ResultCode code = demandType.getResultCode();
        if (code != null)
            return fail(code, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
    }

    public static CommandResult fail(TryResult<?> done) {
        Throws.checkArgument(done.isFail(), "TryDone is success");
        if (done.isTryFailed())
            return fail(done.getResult());
        else
            return fail(done.getCode());
    }

    public static CommandResult fail(TryToDoException exception) {
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.exception2DTO(exception));
    }

}
