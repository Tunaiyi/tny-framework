package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/18 16:23
 **/
public interface DocMethodAccess {

    FunDoc getFunDoc();

    String getDocDesc();

    String getDocText();

    String getDocReturnDesc();

    Method getMethod();

    String getMethodName();

    Class<?> getReturnClass();

    String getReturnClassName();

    List<DocParam> getParamList();

    boolean isHasAnnotation(String annClass);

    Annotation getAnnotation(String annClass);

}
