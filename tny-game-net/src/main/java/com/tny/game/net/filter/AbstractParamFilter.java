package com.tny.game.net.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.dispatcher.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class AbstractParamFilter<A extends Annotation, P> implements ParamFilter {

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
    public ResultCode filter(MethodHolder holder, Request request) {
        List<A> intRangeAnns = holder.getParamAnnotationsByIndex(this.annClass);
        int index = 0;
        Class<?>[] paramClasses = holder.getParamsType();
        for (A rangeAnn : intRangeAnns) {
            if (rangeAnn != null) {
                P param = null;
                if (index == 0) {
                    if (Object.class.isAssignableFrom(this.paramClass)) {
                        param = (P) ((Object) request.getUserID());
                    } else if (Integer.class.isAssignableFrom(this.paramClass)) {
                        param = (P) ((Object) request.getUserID());
                    } else if (Request.class.isAssignableFrom(this.paramClass)) {
                        param = (P) request;
                    }
                } else {
                    param = (P) request.getParameter(index - 1, paramClasses[index]);
                }
                ResultCode result = this.doFilter(holder, request, index, rangeAnn, param);
                if (result != CoreResponseCode.SUCCESS) {
                    return result;
                }
            }
            index++;
        }
        return CoreResponseCode.SUCCESS;
    }

    protected abstract ResultCode doFilter(MethodHolder holder, Request request, int index, A annotation, P param);

}
