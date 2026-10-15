/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
