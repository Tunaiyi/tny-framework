package com.tny.game.net.command.plugins.filter.range;


import com.tny.game.net.command.plugins.filter.range.annotation.*;

public class ByteRangeLimitParamFilter extends RangeLimitParamFilter<ByteRange, Byte> {

    private final static ByteRangeLimitParamFilter INSTANCE = new ByteRangeLimitParamFilter();

    public static ByteRangeLimitParamFilter getInstance() {
        return INSTANCE;
    }

    private ByteRangeLimitParamFilter() {
        super(ByteRange.class);
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
