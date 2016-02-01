package com.tny.game.protoex;

import com.tny.game.protoex.field.runtime.RuntimeProtoExSchema;

/**
 * 默认protoEx类型描述结构上下文
 *
 * @author KGTny
 */
public class DefaultProtoExSchemaContext {

    private final static ProtoExSchemaContext DEFAULT = new RuntimeSchemaContext();

    public static ProtoExSchemaContext getDefault() {
        return DEFAULT;
    }

    private static class RuntimeSchemaContext implements ProtoExSchemaContext {

        private RuntimeSchemaContext() {
        }

        @Override
        public <T> ProtoExSchema<T> getSchema(Class<?> type) {
            ProtoExSchema<T> schema = RuntimeProtoExSchema.getProtoSchema(type);
            if (schema == null)
                throw ProtobufExException.noSchema(type);
            return schema;
        }

        @Override
        public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw) {
            ProtoExSchema<T> schema = RuntimeProtoExSchema.getProtoSchema(protoExID, raw);
            if (schema == null)
                throw ProtobufExException.noSchema(protoExID, raw);
            return schema;
        }

        @Override
        public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw, Class<?> defaultClass) {
            ProtoExSchema<T> schema = RuntimeProtoExSchema.getProtoSchema(protoExID, raw);
            if (schema != null)
                return schema;
            schema = this.getSchema(defaultClass);
            if (schema == null)
                throw ProtobufExException.noSchema(protoExID, raw, defaultClass);
            return schema;
        }

    }

}
