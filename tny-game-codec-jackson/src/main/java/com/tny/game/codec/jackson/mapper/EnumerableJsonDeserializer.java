package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.tny.game.common.enums.*;
import org.springframework.core.ResolvableType;

import java.io.IOException;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumerableJsonDeserializer<T extends Enum<T> & Enumerable<ID>, ID> extends JsonDeserializer<T> implements ContextualDeserializer {

    private Class<T> enumClass;

    private Class<ID> idClass;

    public EnumerableJsonDeserializer() {
    }

    private EnumerableJsonDeserializer(JavaType type) {
        this.enumClass = as(type.getRawClass());
        this.idClass = as(ResolvableType.forClass(Enumerable.class, this.enumClass).resolveGeneric());
        //		if (IntEnumerable.class.isAssignableFrom(this.enumClass)) {
        //			this.idClass = as(Integer.class);
        //		} else if (LongEnumerable.class.isAssignableFrom(this.enumClass)) {
        //			this.idClass = as(Long.class);
        //		} else if (ByteEnumerable.class.isAssignableFrom(this.enumClass)) {
        //			this.idClass = as(Byte.class);
        //		} else {
        //			Asserts.checkArgument(Enumerable.class.isAssignableFrom(this.enumClass),
        //					"Class {} not extends {}", this.enumClass, Enumerable.class);
        //			List<Class<?>> idClasses = ReflectAide.getComponentType(this.enumClass, Enumerable.class);
        //			this.idClass = as(idClasses.get(0));
        //		}
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
        return EnumAide.check(this.enumClass, id);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType type = ctxt.getContextualType() != null ? ctxt.getContextualType() : property.getMember().getType();
        return new EnumerableJsonDeserializer<>(type);
    }

}
