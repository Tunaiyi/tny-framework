package com.tny.game.basics.item.xml.converter;

import com.tny.game.basics.converter.*;
import com.tny.game.expr.*;

import java.util.*;

@SuppressWarnings("rawtypes")
public class String2Enums<T extends Enum<T>> extends String2ExprHolderConverter {

    private final Class<? extends Collection> collectionClass;

    private final Map<String, Object> enumValueMap = new HashMap<>();

    @SafeVarargs
    public String2Enums(ExprHolderFactory exprHolderFactory, Class<? extends Collection> collectionClass, Class<T>... enumClasses) {
        super(exprHolderFactory);
        this.collectionClass = collectionClass;
        for (Class<T> enumClass : enumClasses) {
            for (Enum<?> enumValue : EnumSet.allOf(enumClass))
                this.enumValueMap.put(enumValue.name(), enumValue);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean canConvert(Class type) {
        return type.isAssignableFrom(this.collectionClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object fromString(String str) {
        Collection<?> collection;
        try {
            collection = this.collectionClass.newInstance();
            collection.addAll(this.exprHolderFactory.create(str).createExpr().putAll(this.enumValueMap).execute(List.class));
            return collection;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }

}
