package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.ShortRange;

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

}
