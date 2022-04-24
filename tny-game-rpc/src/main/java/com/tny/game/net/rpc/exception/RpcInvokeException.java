package com.tny.game.net.rpc.exception;

import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/1 7:30 下午
 */
public class RpcInvokeException extends RpcException {

    public RpcInvokeException(ResultCode code) {
        super(code);
    }

    public RpcInvokeException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public RpcInvokeException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public RpcInvokeException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
