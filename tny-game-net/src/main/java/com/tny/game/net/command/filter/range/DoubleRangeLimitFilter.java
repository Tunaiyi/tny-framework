package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.DoubleRange;
import org.springframework.stereotype.Component;

@Component
public class DoubleRangeLimitFilter extends RangeLimitFilter<DoubleRange, Double> {

    protected DoubleRangeLimitFilter() {
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
