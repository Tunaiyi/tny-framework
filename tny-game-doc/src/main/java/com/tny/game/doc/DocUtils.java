/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc;

import com.tny.game.doc.annotation.*;

import java.lang.reflect.*;
import java.util.function.BiConsumer;

import static com.tny.game.common.utils.StringAide.*;

public class DocUtils {

    @SuppressWarnings("unchecked")
    public static <O> void iterateDocMessage(Class<O> clazz, BiConsumer<O, VarDoc> action) {
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && field.getType() == clazz) {
                try {
                    O object = (O)field.get(null);
                    VarDoc varDoc = field.getAnnotation(VarDoc.class);
                    if (varDoc == null) {
                        throw new IllegalAccessException(format("{}.{} 没有 {} 注解", clazz, object, VarDoc.class));
                    }
                    action.accept(object, varDoc);
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

}
