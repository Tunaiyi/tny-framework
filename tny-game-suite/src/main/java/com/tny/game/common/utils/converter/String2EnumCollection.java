package com.tny.game.common.utils.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;

import java.util.*;

@SuppressWarnings("rawtypes")
public class String2EnumCollection<T extends Enum<T>> extends AbstractSingleValueConverter {

    private List<Class<T>> enumClassList = new ArrayList<>();

    private Class<? extends Collection> collectionClass;

    private Map<String, Object> enumValueMap = new HashMap<>();

    @SafeVarargs
    public String2EnumCollection(Class<? extends Collection> collectionClass, Class<T>... enumClasses) {
        super();
        this.collectionClass = collectionClass;
        this.enumClassList.addAll(Arrays.asList(enumClasses));
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
            collection.addAll(MvelFormulaFactory.create(str, FormulaType.EXPRESSION).createFormula().putAll(enumValueMap).execute(List.class));
            return collection;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }

}
