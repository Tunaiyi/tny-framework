package com.tny.game.suite.base.converter;

import com.tny.game.base.converter.String2ExprHolderConverter;
import com.tny.game.expr.ExprHolderFactory;

import java.util.*;

@SuppressWarnings("rawtypes")
public class String2Enums<T extends Enum<T>> extends String2ExprHolderConverter {

    private Class<? extends Collection> collectionClass;

    private Map<String, Object> enumValueMap = new HashMap<>();

    @SafeVarargs
    public String2Enums(ExprHolderFactory exprHolderFactory, Class<? extends Collection> collectionClass, Class<T>... enumClasses) {
        super(exprHolderFactory);
        this.collectionClass = collectionClass;
        for (Class<T> enumClass : enumClasses) {
            for (Enum<?> enumValue : EnumSet.allOf(enumClass))
                enumValueMap.put(enumValue.name(), enumValue);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean canConvert(Class type) {
        return type.isAssignableFrom(collectionClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object fromString(String str) {
        Collection<?> collection;
        try {
            collection = collectionClass.newInstance();
            collection.addAll(exprHolderFactory.create(str).createExpr().putAll(enumValueMap).execute(List.class));
            return collection;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }

}
