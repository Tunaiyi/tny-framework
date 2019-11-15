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
                    O object = (O) field.get(null);
                    VarDoc varDoc = field.getAnnotation(VarDoc.class);
                    if (varDoc == null)
                        throw new IllegalAccessException(format("{}.{} 没有 {} 注解", clazz, object, VarDoc.class));
                    action.accept(object, varDoc);
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

}
