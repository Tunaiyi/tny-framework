package com.tny.game.net.command.plugins.filter;

import com.tny.game.common.result.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-17 04:30
 */
public final class FilterCode {

    private FilterCode() {
    }

    public static ResultCode code(ResultCode defaultCode, int... illegalCodes) {
        ResultCode code = null;
        for (int illegalCode : illegalCodes) {
            if (illegalCode <= 0) {
                continue;
            }
            code = ResultCodes.of(illegalCode);
            if (code != null) {
                return code;
            }
        }
        return defaultCode;
    }

}
