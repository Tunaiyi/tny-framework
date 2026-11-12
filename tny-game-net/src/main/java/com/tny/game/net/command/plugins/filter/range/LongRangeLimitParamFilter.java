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

package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.*;

public class LongRangeLimitParamFilter extends RangeLimitParamFilter<LongRange, Long> {

    private final static LongRangeLimitParamFilter INSTANCE = new LongRangeLimitParamFilter();

    public static LongRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private LongRangeLimitParamFilter() {
        super(LongRange.class);
    }

    @Override
    protected Long getHigh(LongRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Long getLow(LongRange rangeAnn) {
        return rangeAnn.low();
    }

    @Override
    protected int illegalCode(LongRange annotation) {
        return annotation.illegalCode();
    }

}
