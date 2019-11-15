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

}
