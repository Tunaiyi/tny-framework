package com.tny.game.doc;

import com.tny.game.LogUtils;
import com.tny.game.doc.annotation.VarDoc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;

public class DocUtils {


    @SuppressWarnings("unchecked")
    public static <O> void iterateDocMessage(Class<O> clazz, BiConsumer<O, VarDoc> action) {
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && field.getType() == clazz) {
                try {
                    O object = (O) field.get(null);
                    VarDoc varDoc = field.getAnnotation(VarDoc.class);
                    if (varDoc == null)
                        throw new IllegalAccessException(LogUtils.format("{}.{} 没有 {} 注解", clazz, object, VarDoc.class));
                    action.accept(object, varDoc);
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

}
