package com.tny.game.doc.holder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 04:28
 **/
public interface DocFieldAccess extends DocVarAccess {

    String getName();

    default Class<?> getFieldClass() {
        return this.getVarClass();
    }

    default String getFieldClassName() {
        return this.getVarClassName();
    }

    Annotation getFieldIdAnnotation();

    Field getField();

    Object getFieldId();

    boolean isHasAnnotation(String annClass);

    Annotation getAnnotation(String annClass);

}
