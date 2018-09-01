package com.tny.game.net.command.filter.range;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResponseCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.filter.AbstractParamFilter;
import com.tny.game.net.message.*;
import com.tny.game.net.tunnel.Tunnel;

import java.lang.annotation.Annotation;

public abstract class RangeLimitFilter<A extends Annotation, N extends Comparable<N>> extends AbstractParamFilter<Object, A, N> {

    protected RangeLimitFilter(Class<A> annClass, Class<N> numClass) {
        super(annClass, numClass);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message<Object> message, int index, A annotation, N param) {
        N low = this.getLow(annotation);
        N high = this.getHigh(annotation);
        N number = param;
        if (!this.filterRange(low, number, high)) {
            MessageHeader header = message.getHeader();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    message.getUserID(), header.getProtocol(),
                    index, number, low, high);
            return NetResponseCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }

    protected abstract N getHigh(A rangeAnn);

    protected abstract N getLow(A rangeAnn);

    protected boolean filterRange(N low, N number, N high) {
        return number.compareTo(low) >= 0 && number.compareTo(high) <= 0;
    }

}
