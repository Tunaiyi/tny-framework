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

/**
 * Determines how enums are serialized/deserialized. Default is BY_NUMBER. To
 * enable BY_NAME, set the property "protostuff.runtime.enums_by_name=true".
 *
 * @author David Yu
 * @created Oct 20, 2010
 */
public abstract class EnumIO {

    public static int getEnumId(Enum<?> e) {
        if (e instanceof ProtoExEnum) {
            return ((ProtoExEnum)e).getId();
        } else {
            return e.ordinal();
        }
    }

    /**
     * Writes the {@link Enum} to the output.
     */
    public static void writeNotTagTo(ProtoExOutputStream output, Enum<?> e) {
        if (RuntimeEnv.ENUMS_BY_NAME) {
            output.writeString(e.name());
        } else {
            output.writeInt(getEnumId(e));
        }
    }

    public static int getEnumRawProtoExId() {
        if (RuntimeEnv.ENUMS_BY_NAME) {
            return WireFormat.PROTO_ID_STRING;
        } else {
            return WireFormat.PROTO_ID_INT;
        }
    }

    public static <E extends Enum<E>> E readFrom(ProtoExInputStream inputStream, Class<E> clazz) {
        if (RuntimeEnv.ENUMS_BY_NAME) {
            String name = inputStream.readString();
            if (name != null) {
                return Enum.valueOf(clazz, name);
            }
        } else {
            int id = inputStream.readInt();
            boolean protoEnum = ProtoExEnum.class.isAssignableFrom(clazz);
            for (E e : clazz.getEnumConstants()) {
                if (protoEnum) {
                    if (((ProtoExEnum)e).getId() == id) {
                        return e;
                    }
                } else {
                    if (e.ordinal() == id) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

}
