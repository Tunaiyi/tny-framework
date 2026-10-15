/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.protoex;

import com.tny.game.protoex.field.runtime.*;

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
            if (schema == null) {
                throw ProtobufExException.noSchema(type);
            }
            return schema;
        }

        @Override
        public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw) {
            ProtoExSchema<T> schema = RuntimeProtoExSchema.getProtoSchema(protoExID, raw);
            if (schema == null) {
                throw ProtobufExException.noSchema(protoExID, raw);
            }
            return schema;
        }

        @Override
        public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw, Class<?> defaultClass) {
            // ProtoExSchema<T> schema;
            // if (defaultClass != null && defaultClass != Object.class) {
            //     schema = RuntimeProtoExSchema.getProtoSchema(defaultClass);
            //     //protoExID 无效的时候 || schema 与 protoExID 不对应
            //     if (protoExID == 0 || (schema != null && (schema.isRaw() == raw) && schema.getProtoExID() == protoExID))
            //         return schema;
            // }
            // schema = RuntimeProtoExSchema.getProtoSchema(protoExID, raw);
            // if (schema == null)
            //     throw ProtobufExException.noSchema(protoExID, raw, defaultClass);
            // return schema;

            ProtoExSchema<T> schema = RuntimeProtoExSchema.getProtoSchema(protoExID, raw, defaultClass);
            if (schema == null) {
                throw ProtobufExException.noSchema(protoExID, raw, defaultClass);
            }
            return schema;
        }

    }

}
