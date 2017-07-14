package com.tny.game.common.worker;

import com.tny.game.common.result.ResultCode;

public interface Callback<M> {

    void callback(ResultCode code, M message, Throwable cause);

}
