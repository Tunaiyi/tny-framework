package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.net.command.plugins.filter.range.annotation.*;

public class CharRangeLimitParamFilter extends RangeLimitParamFilter<CharRange, Character> {

    private final static CharRangeLimitParamFilter INSTANCE = new CharRangeLimitParamFilter();

    public static CharRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private CharRangeLimitParamFilter() {
        super(CharRange.class);
    }

    @Override
    protected Character getHigh(CharRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Character getLow(CharRange rangeAnn) {
        return rangeAnn.low();
    }

}
