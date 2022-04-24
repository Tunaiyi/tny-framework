package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.*;

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

    @Override
    protected int illegalCode(FloatRange annotation) {
        return annotation.illegalCode();
    }

}
