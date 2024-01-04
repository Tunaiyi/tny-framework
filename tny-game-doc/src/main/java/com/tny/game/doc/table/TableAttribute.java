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

package com.tny.game.doc.table;

import com.tny.game.common.context.*;
import com.tny.game.doc.*;

import java.util.Map;

public interface TableAttribute {

    void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes);

    default String getTemplate() {
        return null;
    }

    default String getOutput() {
        return null;
    }

    Map<String, Object> getContext();

}
