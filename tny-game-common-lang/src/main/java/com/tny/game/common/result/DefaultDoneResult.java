package com.tny.game.common.result;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
class DefaultDoneResult<M> extends BaseDoneResult<M, DefaultDoneResult<M>> {

    DefaultDoneResult(ResultCode code, M returnValue) {
        super(code, returnValue);
    }

    DefaultDoneResult(ResultCode code, M returnValue, String message) {
        super(code, returnValue, message);
    }

}
