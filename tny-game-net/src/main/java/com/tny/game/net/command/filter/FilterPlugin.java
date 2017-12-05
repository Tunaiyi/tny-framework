package com.tny.game.net.command.filter;

import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.InvokeContext;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.filter.range.ByteRangeLimitFilter;
import com.tny.game.net.command.filter.range.CharRangeLimitFilter;
import com.tny.game.net.command.filter.range.DoubleRangeLimitFilter;
import com.tny.game.net.command.filter.range.FloatRangeLimitFilter;
import com.tny.game.net.command.filter.range.IntRangeLimitFilter;
import com.tny.game.net.command.filter.range.LongRangeLimitFilter;
import com.tny.game.net.command.filter.range.ShortRangeLimitFilter;
import com.tny.game.net.command.filter.string.StringLengthLimitFilter;
import com.tny.game.net.command.filter.string.StringPatternLimitFilter;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.base.CoreResponseCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterPlugin<UID> implements ControllerPlugin<UID> {

    private Map<Class<?>, ParamFilter> filterMap = new CopyOnWriteMap<>();

    public FilterPlugin() {
        Collection<ParamFilter> filters = new ArrayList<>();
        filters.add(ByteRangeLimitFilter.getInstance());
        filters.add(CharRangeLimitFilter.getInstance());
        filters.add(DoubleRangeLimitFilter.getInstance());
        filters.add(FloatRangeLimitFilter.getInstance());
        filters.add(IntRangeLimitFilter.getInstance());
        filters.add(LongRangeLimitFilter.getInstance());
        filters.add(ShortRangeLimitFilter.getInstance());
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

    public void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) throws Exception {
        MethodControllerHolder methodHolder = context.getController();
        Set<Class<?>> classSet = methodHolder.getParamAnnotationClass();
        for (Class<?> filterClass : classSet) {
            ParamFilter paramFilter = this.filterMap.get(filterClass);
            if (paramFilter != null) {
                ResultCode resultCode = paramFilter.filter(methodHolder, tunnel, message);
                if (resultCode != CoreResponseCode.SUCCESS) {
                    // 完成 不继续执行
                    context.done(resultCode);
                }
            }
        }
    }
}
