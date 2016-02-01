package com.tny.game.net.filter.range;

import com.tny.game.net.filter.range.annotation.IntRange;
import org.springframework.stereotype.Component;

@Component
public class IntRangeLimitFilter extends RangeLimitFilter<IntRange, Integer> {

    protected IntRangeLimitFilter() {
        super(IntRange.class, Integer.class);
    }

    @Override
    protected Integer getHigh(IntRange rangeAnn) {
        return rangeAnn.high();
    }

    @Override
    protected Integer getLow(IntRange rangeAnn) {
        return rangeAnn.low();
    }

    public static void main(String[] args) {
        IntRangeLimitFilter filter = new IntRangeLimitFilter();
        System.out.println(filter.filterRange(0, 80, Integer.MAX_VALUE));
        System.out.println(filter.filterRange(0, 39, 30));
        System.out.println(filter.filterRange(0, -9, 30));
        System.out.println(filter.filterRange(0, 4, Integer.MAX_VALUE));
    }
}
