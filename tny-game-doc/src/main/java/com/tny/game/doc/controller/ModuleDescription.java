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
