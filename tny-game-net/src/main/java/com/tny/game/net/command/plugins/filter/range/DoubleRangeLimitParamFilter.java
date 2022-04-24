package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.*;

public class DoubleRangeLimitParamFilter extends RangeLimitParamFilter<DoubleRange, Double> {

    private final static DoubleRangeLimitParamFilter INSTANCE = new DoubleRangeLimitParamFilter();

    public static DoubleRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private DoubleRangeLimitParamFilter() {
        super(DoubleRange.class);
    }

    @Override
    protected Double getHigh(DoubleRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Double getLow(DoubleRange rangeAnn) {
        return rangeAnn.low();
    }

    @Override
    protected int illegalCode(DoubleRange annotation) {
        return annotation.illegalCode();
    }

}
