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

package com.tny.game.common.runtime;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/10 2:59 下午
 */
public enum TrackPrintOption {

    /**
     * 全部不打印
     */
    CLOSE(false, false, false),

    /**
     * Start打印
     */
    START_ONLY(true, false, false),

    /**
     * End打印
     */
    END_ONLY(false, true, false),

    /**
     * Start End 打印
     */
    START_END(true, true, false),

    /**
     * 结算打印
     */
    SETTLE(false, false, true),

    /**
     * 所有打印
     */
    ALL(true, true, true),

    //
    ;

    private final boolean onStart;

    private final boolean onEnd;

    private final boolean onSettle;

    TrackPrintOption(boolean onStart, boolean onEnd, boolean onSettle) {
        this.onStart = onStart;
        this.onEnd = onEnd;
        this.onSettle = onSettle;
    }

    boolean isOnStart() {
        return this.onStart;
    }

    boolean isOnEnd() {
        return this.onEnd;
    }

    boolean isOnSettle() {
        return this.onSettle;
    }
}
