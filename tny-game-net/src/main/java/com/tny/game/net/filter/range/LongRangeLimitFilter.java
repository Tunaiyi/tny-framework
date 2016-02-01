package com.tny.game.net.filter.range;

import com.tny.game.net.filter.range.annotation.LongRange;
import org.springframework.stereotype.Component;

@Component
public class LongRangeLimitFilter extends RangeLimitFilter<LongRange, Long> {

    protected LongRangeLimitFilter() {
        super(LongRange.class, Long.class);
    }

    @Override
    protected Long getHigh(LongRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Long getLow(LongRange rangeAnn) {
        return rangeAnn.low();
    }

}
