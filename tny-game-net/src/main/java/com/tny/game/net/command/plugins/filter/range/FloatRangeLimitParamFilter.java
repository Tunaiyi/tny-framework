package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.FloatRange;

public class FloatRangeLimitParamFilter extends RangeLimitParamFilter<FloatRange, Float> {

    private final static FloatRangeLimitParamFilter INSTANCE = new FloatRangeLimitParamFilter();

    public static FloatRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private FloatRangeLimitParamFilter() {
        super(FloatRange.class);
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
