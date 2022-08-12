/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
