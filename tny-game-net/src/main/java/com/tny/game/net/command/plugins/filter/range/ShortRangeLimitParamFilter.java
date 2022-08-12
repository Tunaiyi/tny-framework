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

public class ShortRangeLimitParamFilter extends RangeLimitParamFilter<ShortRange, Short> {

    private final static ShortRangeLimitParamFilter INSTANCE = new ShortRangeLimitParamFilter();

    public static ShortRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private ShortRangeLimitParamFilter() {
        super(ShortRange.class);
    }

    @Override
    protected Short getHigh(ShortRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Short getLow(ShortRange rangeAnn) {
        return rangeAnn.low();
    }

    @Override
    protected int illegalCode(ShortRange annotation) {
        return annotation.illegalCode();
    }

}
