package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.FloatRange;

public class FloatRangeLimitFilter extends RangeLimitFilter<FloatRange, Float> {

    private final static FloatRangeLimitFilter INSTANCE = new FloatRangeLimitFilter();

    public static FloatRangeLimitFilter getInstance() {
        return INSTANCE;
    }

    private FloatRangeLimitFilter() {
        super(FloatRange.class, Float.class);
    }

    @Override
    protected Float getHigh(FloatRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Float getLow(FloatRange rangeAnn) {
        return rangeAnn.low();
    }

}
