package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.ByteRange;

public class ByteRangeLimitFilter extends RangeLimitFilter<ByteRange, Byte> {

    private final static ByteRangeLimitFilter INSTANCE = new ByteRangeLimitFilter();

    public static ByteRangeLimitFilter getInstance() {
        return INSTANCE;
    }

    private ByteRangeLimitFilter() {
        super(ByteRange.class, Byte.class);
    }

    @Override
    protected Byte getHigh(ByteRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Byte getLow(ByteRange rangeAnn) {
        return rangeAnn.low();
    }

}
