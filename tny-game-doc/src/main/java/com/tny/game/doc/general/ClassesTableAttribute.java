package com.tny.game.doc.general;

import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.table.*;

import java.util.*;

public class ClassesTableAttribute implements TableAttribute {

    private final List<Class<?>> classes = new ArrayList<>();

    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
        this.classes.add(clazz);
    }

    @Override
    public Map<String, Object> getContext() {
        return MapBuilder.<String, Object>newBuilder()
                .put("classes", classes)
                .build();
    }

}
