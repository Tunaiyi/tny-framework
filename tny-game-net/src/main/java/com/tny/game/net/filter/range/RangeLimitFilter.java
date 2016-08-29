package com.tny.game.net.filter.range;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.filter.AbstractParamFilter;
import com.tny.game.net.base.CoreResponseCode;

import java.lang.annotation.Annotation;

public abstract class RangeLimitFilter<A extends Annotation, N extends Comparable<N>> extends AbstractParamFilter<A, N> {

    protected RangeLimitFilter(Class<A> annClass, Class<N> numClass) {
        super(annClass, numClass);
    }

    @Override
    protected ResultCode doFilter(MethodHolder holder, Request request, int index, A annotation, N param) {
        N low = this.getLow(annotation);
        N high = this.getHigh(annotation);
        N number = param;
        if (!this.filterRange(low, number, high)) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    request.getUserID(), request.getProtocol(),
                    index, number, low, high);
            return CoreResponseCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }

    protected abstract N getHigh(A rangeAnn);

    protected abstract N getLow(A rangeAnn);

    protected boolean filterRange(N low, N number, N high) {
        return number.compareTo(low) >= 0 && number.compareTo(high) <= 0;
    }

}
