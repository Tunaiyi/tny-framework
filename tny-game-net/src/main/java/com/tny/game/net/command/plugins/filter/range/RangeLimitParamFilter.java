package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.plugins.filter.AbstractParamFilter;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.Tunnel;

import java.lang.annotation.Annotation;

public abstract class RangeLimitParamFilter<A extends Annotation, N extends Comparable<N>> extends AbstractParamFilter<Object, A, N> {

    protected RangeLimitParamFilter(Class<A> annClass) {
        super(annClass);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message<Object> message, int index, A annotation, N param) {
        N low = this.getLow(annotation);
        N high = this.getHigh(annotation);
        if (!this.filterRange(low, param, high)) {
            MessageHead head = message.getHead();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    message.getUserID(), head.getId(),
                    index, param, low, high);
            return NetResultCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }

    protected abstract N getHigh(A rangeAnn);

    protected abstract N getLow(A rangeAnn);

    protected boolean filterRange(N low, N number, N high) {
        return number.compareTo(low) >= 0 && number.compareTo(high) <= 0;
    }

}
