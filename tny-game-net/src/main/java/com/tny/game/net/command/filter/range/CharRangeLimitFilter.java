package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.CharRange;

public class CharRangeLimitFilter extends RangeLimitFilter<CharRange, Character> {

    private final static CharRangeLimitFilter INSTANCE = new CharRangeLimitFilter();

    public static CharRangeLimitFilter getInstance() {
        return INSTANCE;
    }

    private CharRangeLimitFilter() {
        super(CharRange.class, Character.class);
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
