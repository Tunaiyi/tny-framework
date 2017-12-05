package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.LongRange;

public class LongRangeLimitFilter extends RangeLimitFilter<LongRange, Long> {

    private final static LongRangeLimitFilter INSTANCE = new LongRangeLimitFilter();

    public static LongRangeLimitFilter getInstance() {
        return INSTANCE;
    }

    private LongRangeLimitFilter() {
        super(LongRange.class, Long.class);
    }

    @Override
    protected Long getHigh(LongRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Long getLow(LongRange rangeAnn) {
        return rangeAnn.low();
    }

}
