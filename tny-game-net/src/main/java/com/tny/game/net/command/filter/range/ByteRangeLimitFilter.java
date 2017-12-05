package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.ByteRange;
import org.springframework.stereotype.Component;

@Component
public class ByteRangeLimitFilter extends RangeLimitFilter<ByteRange, Byte> {

    protected ByteRangeLimitFilter() {
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
