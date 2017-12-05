package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.DoubleRange;

public class DoubleRangeLimitFilter extends RangeLimitFilter<DoubleRange, Double> {

    private final static DoubleRangeLimitFilter INSTANCE = new DoubleRangeLimitFilter();

    public static DoubleRangeLimitFilter getInstance() {
        return INSTANCE;
    }

    private  DoubleRangeLimitFilter() {
        super(DoubleRange.class, Double.class);
    }

    @Override
    protected Double getHigh(DoubleRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Double getLow(DoubleRange rangeAnn) {
        return rangeAnn.low();
    }

}
