/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.command.plugins.filter.range;

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.lang.annotation.Annotation;

import static com.tny.game.net.command.plugins.filter.FilterCode.*;

public abstract class RangeLimitParamFilter<A extends Annotation, N extends Comparable<N>> extends AbstractParamFilter<A, N> {

    protected RangeLimitParamFilter(Class<A> annClass) {
        super(annClass);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel tunnel, Message message, int index, A annotation, N param) {
        N low = this.getLow(annotation);
        N high = this.getHigh(annotation);
        if (!this.filterRange(low, param, high)) {
            MessageHead head = message.getHead();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    tunnel.getIdentify(), head.getId(),
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
