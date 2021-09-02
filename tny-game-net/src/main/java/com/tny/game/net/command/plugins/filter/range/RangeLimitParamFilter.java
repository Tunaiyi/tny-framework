package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.lang.annotation.Annotation;

import static com.tny.game.net.command.plugins.filter.FilterCode.*;

public abstract class RangeLimitParamFilter<A extends Annotation, N extends Comparable<N>> extends AbstractParamFilter<Object, A, N> {

	protected RangeLimitParamFilter(Class<A> annClass) {
		super(annClass);
	}

	@Override
	protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message message, int index, A annotation, N param) {
		N low = this.getLow(annotation);
		N high = this.getHigh(annotation);
		if (!this.filterRange(low, param, high)) {
			MessageHead head = message.getHead();
			LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
					tunnel.getUserId(), head.getId(),
					index, param, low, high);
			return code(NetResultCode.SERVER_ILLEGAL_PARAMETERS, illegalCode(annotation));
		}
		return ResultCode.SUCCESS;
	}

	protected abstract int illegalCode(A annotation);

	protected abstract N getHigh(A rangeAnn);

	protected abstract N getLow(A rangeAnn);

	protected boolean filterRange(N low, N number, N high) {
		return number.compareTo(low) >= 0 && number.compareTo(high) <= 0;
	}

}
