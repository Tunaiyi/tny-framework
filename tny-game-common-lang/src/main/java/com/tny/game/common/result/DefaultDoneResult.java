/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
