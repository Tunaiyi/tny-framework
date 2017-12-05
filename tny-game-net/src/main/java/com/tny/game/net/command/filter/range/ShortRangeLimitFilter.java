package com.tny.game.net.command.filter.range;

import com.tny.game.net.command.filter.range.annotation.ShortRange;
import org.springframework.stereotype.Component;

@Component
public class ShortRangeLimitFilter extends RangeLimitFilter<ShortRange, Short> {

    protected ShortRangeLimitFilter() {
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
