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

package com.tny.game.doc.controller;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;

public class ModuleDescription extends ClassDescription {

    private static final Map<Class<?>, ModuleDescription> DESCRIPTION_MAP = new ConcurrentHashMap<>();

    private final List<OperationDescription> operationList;

    public static ModuleDescription create(DocClass docClass, TypeFormatter typeFormatter) {
        ModuleDescription description = DESCRIPTION_MAP.get(docClass.getRawClass());
        ModuleDescription old;
        if (description != null) {
            if (description.getDocClassName().equals(docClass.getDocClassName())) {
                return description;
            }
            throw new IllegalArgumentException(
                    format("{} 类 与 {} 类 Module 已存在", description.getRawClassName(), docClass.getRawClassName()));
        } else {
            description = new ModuleDescription(docClass, typeFormatter);
            old = DESCRIPTION_MAP.putIfAbsent(docClass.getRawClass(), description);
            if (old != null) {
                throw new IllegalArgumentException(
                        format("{} 类 与 {} 类 Module 已存在", description.getRawClassName(), docClass.getRawClassName()));
            } else {
                return description;
            }
        }
    }

    private ModuleDescription(DocClass docClass, TypeFormatter typeFormatter) {
        super(docClass);
        Map<Integer, OperationDescription> fieldMap = new HashMap<>();
        List<OperationDescription> operationList = new ArrayList<>();
        for (DocMethod method : docClass.getMethodList()) {
            OperationDescription description = new OperationDescription(docClass.getRawClass(), method, typeFormatter);
            operationList.add(description);
            OperationDescription old = fieldMap.put(description.getOpId(), description);
            if (old != null) {
                throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 OpID 都为 {}",
                        docClass.getRawClass(), description.getMethodName(), old.getMethodName(), description.getOpId()));
            }
        }
        operationList.sort(Comparator.comparing(OperationDescription::getMethodName));
        this.operationList = Collections.unmodifiableList(operationList);
    }

    public List<OperationDescription> getOperationList() {
        return this.operationList;
    }

}
