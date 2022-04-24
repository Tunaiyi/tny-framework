package com.tny.game.data.mongodb.mapper;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.*;
import org.springframework.data.annotation.Id;

import java.lang.annotation.Annotation;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 4:02 下午
 */
public class MongoIdIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean isAnnotationBundle(Annotation ann) {
        if (ann.annotationType() == Id.class) {
            return true;
        }
        return super.isAnnotationBundle(ann);
    }

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        PropertyName value = getPropertyName(a);
        if (value != null) {
            return value;
        }
        return super.findNameForSerialization(a);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        PropertyName value = getPropertyName(a);
        if (value != null) {
            return value;
        }
        return super.findNameForDeserialization(a);
    }

    private PropertyName getPropertyName(Annotated af) {
        Id id = af.getAnnotation(Id.class);
        if (id != null) {
            return PropertyName.construct("_id");
        }
        return null;
    }

}
