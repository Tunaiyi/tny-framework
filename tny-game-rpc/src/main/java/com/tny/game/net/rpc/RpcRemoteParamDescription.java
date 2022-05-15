package com.tny.game.net.rpc;

import com.tny.game.common.result.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.annotation.*;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.tny.game.net.command.dispatcher.ParamMode.*;

/**
 * Rpc参数描述
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/1 16:58
 **/
class RpcRemoteParamDescription {

    private int index = -1;

    private boolean require = true;

    private ParamMode mode = NONE;

    private final Class<?> paramClass;

    private final AnnotationHolder annotationHolder;

    private boolean route = false;

    RpcRemoteParamDescription(RpcRemoteMethod method, Class<?> paramClass, List<Annotation> paramAnnotations, ParamIndexCreator indexCreator) {
        this.paramClass = paramClass;
        this.annotationHolder = new AnnotationHolder(paramAnnotations);
        RpcOptional optional = this.annotationHolder.getAnnotation(RpcOptional.class);
        if (optional != null) {
            this.require = false;
        }
        if (MessageHeader.class.isAssignableFrom(paramClass)) {
            this.mode = HEADER;
        } else if (ResultCode.class.isAssignableFrom(paramClass)) {
            this.mode = CODE;
        } else {
            for (Class<?> annotationClass : this.annotationHolder.getAnnotationClasses()) {
                if (annotationClass == RpcBody.class) {
                    this.mode = BODY;
                } else if (annotationClass == RpcParam.class) {
                    RpcParam param = this.annotationHolder.getAnnotation(RpcParam.class);
                    if (param.value() < 0) {
                        this.index = indexCreator.peek();
                    } else {
                        this.index = indexCreator.use(param.value());
                    }
                    this.mode = PARAM;
                }
                if (annotationClass == RpcCode.class) {
                    this.mode = CODE_NUM;
                } else if (annotationClass == RpcIgnore.class) {
                    this.mode = IGNORE;
                } else if (annotationClass == RpcRouteParam.class) {
                    this.route = true;
                } else if (annotationClass == RpcFrom.class) {
                    if (Messager.class.isAssignableFrom(paramClass)) {
                        this.mode = SENDER;
                    }
                    if (RpcServicer.class.isAssignableFrom(paramClass)) {
                        this.mode = FROM_SERVICE;
                    }
                } else if (annotationClass == RpcTo.class) {
                    if (Messager.class.isAssignableFrom(paramClass)) {
                        this.mode = RECEIVER;
                    }
                    if (RpcServicer.class.isAssignableFrom(paramClass)) {
                        this.mode = TO_SERVICE;
                    }
                }
            }
        }
        if (this.mode == NONE && require) {
            if (method.getMode() == MessageMode.REQUEST) {
                this.index = indexCreator.peek();
                this.mode = PARAM;
            } else {
                this.mode = BODY;
            }
        }
    }

    boolean isRequire() {
        return this.require;
    }

    boolean isRoute() {
        return route;
    }

    int getIndex() {
        return index;
    }

    ParamMode getMode() {
        return mode;
    }

    AnnotationHolder getAnnotationHolder() {
        return annotationHolder;
    }

    Class<?> getParamClass() {
        return paramClass;
    }

}
