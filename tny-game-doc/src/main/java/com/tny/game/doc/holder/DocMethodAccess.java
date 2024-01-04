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
