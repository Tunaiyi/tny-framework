/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
