package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.tny.game.common.enums.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.utils.*;

import java.io.IOException;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumIDJsonDeserializer<T extends Enum<T> & EnumIdentifiable<ID>, ID> extends JsonDeserializer<T> implements ContextualDeserializer {

    private Class<T> enumClass;

    private Class<ID> idClass;

    public EnumIDJsonDeserializer() {
    }

    private EnumIDJsonDeserializer(JavaType type) {
        this.enumClass = as(type.getRawClass());
        Asserts.checkArgument(EnumIdentifiable.class.isAssignableFrom(this.enumClass),
                "Class {} not extends {}", this.enumClass, EnumIdentifiable.class);
        List<Class<?>> idClasses = ReflectAide.getComponentType(this.enumClass, EnumIdentifiable.class);
        this.idClass = as(idClasses.get(0));
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ID id;
        switch (p.getCurrentToken()) {
            case VALUE_EMBEDDED_OBJECT:
                id = as(p.getEmbeddedObject(), this.idClass);
                break;
            case VALUE_STRING:
                id = as(p.getValueAsString(), this.idClass);
                break;
            case VALUE_NUMBER_INT:
                id = as(p.getIntValue(), this.idClass);
                break;
            case VALUE_NUMBER_FLOAT:
                id = as(p.getFloatValue(), this.idClass);
                break;
            case VALUE_TRUE:
            case VALUE_FALSE:
                id = as(p.getBooleanValue(), this.idClass);
                break;
            default:
                return null;
        }
        if (id == null) {
            return null;
        }
        return EnumAide.of(this.enumClass, id);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType type = ctxt.getContextualType() != null ? ctxt.getContextualType() : property.getMember().getType();
        return new EnumIDJsonDeserializer<>(type);
    }

}
