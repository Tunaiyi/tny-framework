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
package com.tny.game.net.command.plugins.filter;

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class AbstractParamFilter<A extends Annotation, P> implements ParamFilter {

    protected final static Logger LOGGER = LoggerFactory.getLogger(ParamFilter.class);

    private final Class<A> annClass;

    protected AbstractParamFilter(Class<A> annClass) {
        this.annClass = annClass;
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return this.annClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultCode filter(MethodControllerHolder holder, Tunnel tunnel, Message message) throws RpcInvokeException {
        List<A> annotations = holder.getAnnotationsOfParametersByType(this.annClass);
        int index = 0;
        Object body = message.bodyAs(Object.class);
        for (A an : annotations) {
            if (an != null) {
                P param = (P) holder.getParameterValue(index, as(tunnel), message, body);
                ResultCode result = this.doFilter(holder, tunnel, message, index, an, param);
                if (result != NetResultCode.SUCCESS) {
                    return result;
                }
            }
            index++;
        }
        return NetResultCode.SUCCESS;
    }

    protected abstract ResultCode doFilter(MethodControllerHolder holder, Tunnel tunnel, Message message, int index, A annotation, P param);

}
