package com.tny.game.doc.holder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/18 17:29
 **/
public interface DocParamAccess extends DocVarAccess {

    String getName();

    Parameter getParameter();

    default Class<?> getParamClass() {
        return getVarClass();
    }

    default String getParamClassName() {
        return getVarClassName();
    }

    boolean isHasAnnotation(String annClass);

    Annotation getAnnotation(String annClass);

}
