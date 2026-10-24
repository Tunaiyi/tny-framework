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

public class ByteRangeLimitParamFilter extends RangeLimitParamFilter<ByteRange, Byte> {

    private final static ByteRangeLimitParamFilter INSTANCE = new ByteRangeLimitParamFilter();

    public static ByteRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private ByteRangeLimitParamFilter() {
        super(ByteRange.class);
    }

    @Override
    protected int illegalCode(ByteRange annotation) {
        return annotation.illegalCode();
    }

    @Override
    protected Byte getHigh(ByteRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Byte getLow(ByteRange rangeAnn) {
        return rangeAnn.low();
    }

}
