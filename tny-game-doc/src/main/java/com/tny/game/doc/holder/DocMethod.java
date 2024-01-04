/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

public class DocMethod implements DocMethodAccess {

    private final FunDoc funDoc;

    private final String docDesc;

    private final String docText;

    private final String docReturnDesc;

    private final Class<?> returnClass;

    private final String returnClassName;

    private final Method method;

    private final String methodName;

    private final List<DocParam> paramList;

    public DocMethod(Method method) {
        this.funDoc = method.getAnnotation(FunDoc.class);
        this.method = method;
        this.methodName = method.getName();
        this.docDesc = funDoc.value();
        this.docText = funDoc.text();
        this.docReturnDesc = funDoc.returnDesc();
        List<DocParam> paramList = new ArrayList<>();
        Parameter[] params = method.getParameters();
        for (Parameter param : params) {
            VarDoc paramDoc = param.getAnnotation(VarDoc.class);
            if (paramDoc == null) {
                continue;
            }
            paramList.add(DocParam.create(param));
        }
        this.returnClass = method.getReturnType();
        this.returnClassName = this.returnClass.getSimpleName();
        //        if (docReturnClass == null || docReturnClass == Object.class) {
        //            docReturnClass = this.returnClass;
        //        }
        //        this.docReturnClass = docReturnClass;
        this.paramList = Collections.unmodifiableList(paramList);
    }

    public static DocMethod create(Method method) {
        FunDoc funDoc = method.getAnnotation(FunDoc.class);
        if (funDoc == null) {
            return null;
        }
        return new DocMethod(method);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getParamsAnnotation(Annotation[] paramAnnotation, Class<VarDoc> class1) {
        for (Annotation an : paramAnnotation) {
            if (class1.isInstance(an)) {
                return (T) an;
            }
        }
        return null;
    }

    @Override
    public FunDoc getFunDoc() {
        return funDoc;
    }

    @Override
    public String getDocDesc() {
        return docDesc;
    }

    @Override
    public String getDocText() {
        return docText;
    }

    @Override
    public String getDocReturnDesc() {
        return docReturnDesc;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?> getReturnClass() {
        return returnClass;
    }

    @Override
    public String getReturnClassName() {
        return returnClassName;
    }

    @Override
    public List<DocParam> getParamList() {
        return paramList;
    }

    @Override
    public boolean isHasAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return this.getMethod().getAnnotation(annotationClass) != null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Annotation getAnnotation(String annClass) {
        try {
            Class<? extends Annotation> annotationClass = as(Thread.currentThread().getContextClassLoader().loadClass(annClass));
            return this.getMethod().getAnnotation(annotationClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
