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
                if (param == null) {
                    // 空可选值放行（message-checking 契约）：取值类检查不做 null 比较，
                    // 必填参数的缺失由参数装配环节处置
                    index++;
                    continue;
                }
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
