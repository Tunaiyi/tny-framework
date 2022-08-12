/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.*;

public class IntRangeLimitParamFilter extends RangeLimitParamFilter<IntRange, Integer> {

    private final static IntRangeLimitParamFilter INSTANCE = new IntRangeLimitParamFilter();

    public static IntRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private IntRangeLimitParamFilter() {
        super(IntRange.class);
    }

    @Override
    protected Integer getHigh(IntRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Integer getLow(IntRange rangeAnn) {
        return rangeAnn.low();
    }

    @Override
    protected int illegalCode(IntRange annotation) {
        return annotation.illegalCode();
    }

}
