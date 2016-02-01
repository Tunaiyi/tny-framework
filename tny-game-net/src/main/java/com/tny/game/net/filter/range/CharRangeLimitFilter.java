package com.tny.game.net.filter.range;

import com.tny.game.net.filter.range.annotation.CharRange;
import org.springframework.stereotype.Component;

@Component
public class CharRangeLimitFilter extends RangeLimitFilter<CharRange, Character> {

    protected CharRangeLimitFilter() {
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
