package com.tny.game.net.command.plugins.filter;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
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

public class ParamFilterPlugin<UID> implements VoidInvokeCommandPlugin<UID> {

    private final Map<Class<?>, ParamFilter<?>> filterMap = new CopyOnWriteMap<>();

    public ParamFilterPlugin() {
        Collection<ParamFilter<?>> filters = new ArrayList<>();
        filters.add(ByteRangeLimitParamFilter.getInstance());
        filters.add(CharRangeLimitParamFilter.getInstance());
        filters.add(DoubleRangeLimitParamFilter.getInstance());
        filters.add(FloatRangeLimitParamFilter.getInstance());
        filters.add(IntRangeLimitParamFilter.getInstance());
        filters.add(LongRangeLimitParamFilter.getInstance());
        filters.add(ShortRangeLimitParamFilter.getInstance());
        filters.add(TextLengthLimitFilter.getInstance());
        filters.add(TextPatternLimitFilter.getInstance());
        this.addParamFilters(filters);
    }

    protected void addParamFilters(Collection<ParamFilter<?>> filters) {
        Map<Class<?>, ParamFilter<?>> maps = filters.stream().collect(Collectors.toMap(ParamFilter::getAnnotationClass, ObjectAide::self));
        this.filterMap.putAll(maps);
    }

    protected void addParamFilter(ParamFilter<?> filter) {
        this.filterMap.put(filter.getClass(), filter);
    }

    @Override
    public void doExecute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) {
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
                    context.doneAndIntercept(CommandResults.fail(e.getResultCode(), e.getBody()));
                }
            }
        }
    }

}
