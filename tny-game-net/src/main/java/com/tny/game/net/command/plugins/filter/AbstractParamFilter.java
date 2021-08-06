package com.tny.game.net.command.plugins.filter;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class AbstractParamFilter<UID, A extends Annotation, P> implements ParamFilter<UID> {

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
    public ResultCode filter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message message) throws CommandException {
        List<A> annotations = holder.getParamsAnnotationsByType(this.annClass);
        int index = 0;
        Object body = message.bodyAs(Object.class);
        for (A an : annotations) {
            if (an != null) {
                P param = (P)holder.getParameterValue(index, as(tunnel), message, body);
                ResultCode result = this.doFilter(holder, tunnel, message, index, an, param);
                if (result != NetResultCode.SUCCESS) {
                    return result;
                }
            }
            index++;
        }
        return NetResultCode.SUCCESS;
    }

    protected abstract ResultCode doFilter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message message, int index, A annotation, P param);

}
