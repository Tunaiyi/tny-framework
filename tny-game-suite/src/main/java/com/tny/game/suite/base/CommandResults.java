package com.tny.game.suite.base;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.exception.TryToDoException;
import com.tny.game.base.item.behavior.DemandResult;
import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.base.utlis.TryDone;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.command.CommandResult;
import com.tny.game.suite.base.dto.TryToDoFailDTO;

public class CommandResults extends ResultFactory {

    public static CommandResult fail(TryToDoResult tryToDoResult) {
        DemandResult result = tryToDoResult.getFailResult();
        DemandType demandType = result.getDemandType();
        ResultCode code = demandType.getResultCode();
        if (code != null)
            return fail(code, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.tryToDoResult2DTO(tryToDoResult));
    }

    public static CommandResult fail(TryDone<?> done) {
        ExceptionUtils.checkArgument(done.isFail(), "TryDone is success");
        if (done.isTryFailed())
            return fail(done.getTryResult());
        else
            return fail(done.getCode());
    }

    public static CommandResult fail(TryToDoException exception) {
        return fail(ItemResultCode.TRY_TO_DO_FAIL, TryToDoFailDTO.exception2DTO(exception));
    }

}
