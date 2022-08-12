package com.tny.game.common.act;

import com.tny.game.common.result.*;

/**
 * 行动结果状态
 * <p>
 *
 * @author kgtny
 * @date 2022/8/12 17:02
 **/
public interface ActStatus {

    ResultCode resultCode();

    default int getCode() {
        return resultCode().getCode();
    }

    default boolean isSuccess() {
        return resultCode().isSuccess();
    }

    default boolean isFailure() {
        return resultCode().isFailure();
    }

    default String getMessage() {
        return resultCode().getMessage();
    }

    default ResultLevel getLevel() {
        return resultCode().getLevel();
    }

    default String message(Object... messageParams) {
        return resultCode().message(messageParams);
    }

}
