package com.tny.game.net.command.plugins.filter;

import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.range.*;
import com.tny.game.net.command.plugins.filter.string.*;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.message.Message;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

public class ParamFilterPlugin<UID> implements VoidControllerPlugin<UID> {

    private Map<Class<?>, ParamFilter> filterMap = new CopyOnWriteMap<>();

    public ParamFilterPlugin() {
        Collection<ParamFilter> filters = new ArrayList<>();
        filters.add(ByteRangeLimitParamFilter.getInstance());
        filters.add(CharRangeLimitParamFilter.getInstance());
        filters.add(DoubleRangeLimitParamFilter.getInstance());
        filters.add(FloatRangeLimitParamFilter.getInstance());
        filters.add(IntRangeLimitParamFilter.getInstance());
        filters.add(LongRangeLimitParamFilter.getInstance());
        filters.add(ShortRangeLimitParamFilter.getInstance());
        filters.add(StringLengthLimitFilter.getInstance());
        filters.add(StringPatternLimitFilter.getInstance());
        this.addParamFilters(filters);
    }

    protected void addParamFilters(Collection<ParamFilter> filters) {
        Map<Class<?>, ParamFilter> maps = filters.stream().collect(Collectors.toMap(ParamFilter::getAnnotationClass, ObjectAide::self));
        filterMap.putAll(maps);
    }

    protected void addParamFilter(ParamFilter<?> filter) {
        filterMap.put(filter.getClass(), filter);
    }

    @Override
    public void doExecute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) throws Exception {
        MethodControllerHolder methodHolder = context.getController();
        Set<Class<?>> classSet = methodHolder.getParamAnnotationClass();
        for (Class<?> filterClass : classSet) {
            ParamFilter<UID> paramFilter = as(this.filterMap.get(filterClass));
            if (paramFilter != null) {
                try {
                    ResultCode resultCode = paramFilter.filter(methodHolder, tunnel, message);
                    if (resultCode != NetResultCode.SUCCESS) {
                        // 完成 不继续执行
                        context.doneAndIntercept(resultCode);
                    }
                } catch (CommandException e) {
                    context.doneAndIntercept(ResultFactory.fail(e.getResultCode(), e.getBody()));
                }
            }
        }
    }
}
