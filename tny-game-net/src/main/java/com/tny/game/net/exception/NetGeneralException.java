package com.tny.game.net.exception;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;

public class NetGeneralException extends ResultCodeRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NetGeneralException(ResultCode code) {
        super(code);
    }

    public NetGeneralException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public NetGeneralException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public NetGeneralException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
