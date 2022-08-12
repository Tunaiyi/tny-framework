/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
