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
