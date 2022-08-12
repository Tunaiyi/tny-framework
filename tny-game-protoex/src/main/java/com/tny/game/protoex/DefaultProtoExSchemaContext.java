/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
