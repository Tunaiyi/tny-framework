package com.tny.game.net.command.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class AbstractParamFilter<UID, A extends Annotation, P> implements ParamFilter<UID> {

    protected final static Logger LOGGER = LoggerFactory.getLogger(ParamFilter.class);

    protected final Class<P> paramClass;
    protected final Class<A> annClass;

    protected AbstractParamFilter(Class<A> annClass, Class<P> paramClass) {
        this.annClass = annClass;
        this.paramClass = paramClass;
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return this.annClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultCode filter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message<UID> message) throws DispatchException {
        List<A> annotations = holder.getParamsAnnotationsByType(this.annClass);
        int index = 0;
        Object body = message.getBody(Object.class);
        for (A an : annotations) {
            if (an != null) {
                P param = (P) holder.getParameterValue(index, tunnel, message, body);
                ResultCode result = this.doFilter(holder, tunnel, message, index, an, param);
                if (result != CoreResponseCode.SUCCESS) {
                    return result;
                }
            }
            index++;
        }
        return CoreResponseCode.SUCCESS;
    }

    protected abstract ResultCode doFilter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message<UID> message, int index, A annotation, P param);

}
