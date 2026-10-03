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

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.command.plugins.filter.range.*;
import com.tny.game.net.command.plugins.filter.text.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

public class ParamFilterPlugin implements VoidInvokeCommandPlugin {

    private final Map<Class<?>, ParamFilter> filterMap = new CopyOnWriteMap<>();

    public ParamFilterPlugin() {
        Collection<ParamFilter> filters = new ArrayList<>();
        filters.add(ByteRangeLimitParamFilter.getInstance());
        filters.add(CharRangeLimitParamFilter.getInstance());
        filters.add(DoubleRangeLimitParamFilter.getInstance());
        filters.add(FloatRangeLimitParamFilter.getInstance());
        filters.add(IntRangeLimitParamFilter.getInstance());
        filters.add(LongRangeLimitParamFilter.getInstance());
        filters.add(ShortRangeLimitParamFilter.getInstance());
        filters.add(TextLengthLimitFilter.getInstance());
        filters.add(TextPatternLimitFilter.getInstance());
        // @TextCheck 此前无默认注册（注解静默失效，message-checking 契约）；词表经 setWordsFilters 注入
        filters.add(new TextCheckFilter());
        this.addParamFilters(filters);
    }

    protected void addParamFilters(Collection<ParamFilter> filters) {
        Map<Class<?>, ParamFilter> maps = filters.stream().collect(Collectors.toMap(ParamFilter::getAnnotationClass, ObjectAide::self));
        this.filterMap.putAll(maps);
    }

    protected void addParamFilter(ParamFilter filter) {
        // 原以 filter.getClass() 建 key，与查询侧（注解类）永不相交——注册的过滤器不可达
        this.filterMap.put(filter.getAnnotationClass(), filter);
    }

    /**
     * 启动期覆盖校验：被业务使用的校验注解必须有对应检查器，缺失即 fail-fast
     * （message-checking"注解与检查器覆盖关系启动期校验"契约）。
     */
    public void checkCoverage(Set<Class<?>> usedAnnotationClasses) {
        List<Class<?>> missing = usedAnnotationClasses.stream()
                .filter(ann -> !this.filterMap.containsKey(ann))
                .collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new IllegalStateException("参数校验注解缺少对应检查器: " + missing);
        }
    }

    @Override
    public void doExecute(Tunnel tunnel, Message message, RpcInvokeContext context) {
        MethodControllerHolder methodHolder = context.getController();
        Set<Class<?>> classSet = methodHolder.getParamAnnotationClass();
        for (Class<?> filterClass : classSet) {
            ParamFilter paramFilter = as(this.filterMap.get(filterClass));
            if (paramFilter != null) {
                try {
                    ResultCode resultCode = paramFilter.filter(methodHolder, tunnel, message);
                    if (resultCode != NetResultCode.SUCCESS) {
                        // 完成 不继续执行
                        context.doneAndIntercept(resultCode);
                    }
                } catch (RpcInvokeException e) {
                    context.doneAndIntercept(RpcResults.fail(e.getCode(), e.getBody()));
                }
            }
        }
    }

}
