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
