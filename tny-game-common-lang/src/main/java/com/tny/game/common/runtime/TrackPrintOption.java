/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
