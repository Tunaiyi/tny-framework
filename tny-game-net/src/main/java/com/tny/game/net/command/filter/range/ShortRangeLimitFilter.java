package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.ShortRange;

public class ShortRangeLimitFilter extends RangeLimitFilter<ShortRange, Short> {

    private final static ShortRangeLimitFilter INSTANCE = new ShortRangeLimitFilter();

    public static ShortRangeLimitFilter getInstance() {
        return INSTANCE;
    }

    private  ShortRangeLimitFilter() {
        super(ShortRange.class, Short.class);
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
