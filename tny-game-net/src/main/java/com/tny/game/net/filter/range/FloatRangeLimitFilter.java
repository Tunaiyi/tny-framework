package com.tny.game.net.filter.range;

import com.tny.game.net.filter.range.annotation.FloatRange;
import org.springframework.stereotype.Component;

@Component
public class FloatRangeLimitFilter extends RangeLimitFilter<FloatRange, Float> {

    protected FloatRangeLimitFilter() {
        super(FloatRange.class, Float.class);
    }

    @Override
    protected Float getHigh(FloatRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Float getLow(FloatRange rangeAnn) {
        return rangeAnn.low();
    }

}
