//========================================================================
//Copyright 2007-2010 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.tny.game.protoex;

/**
 * Determines how enums are serialized/deserialized. Default is BY_NUMBER. To
 * enable BY_NAME, set the property "protostuff.runtime.enums_by_name=true".
 *
 * @author David Yu
 * @created Oct 20, 2010
 */
public abstract class EnumIO {

    public static int getEnumID(Enum<?> e) {
        if (e instanceof ProtoExEnum) {
            return ((ProtoExEnum) e).getID();
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
            output.writeInt(getEnumID(e));
        }
    }

    public static int getEnumRawProtoExID() {
        if (RuntimeEnv.ENUMS_BY_NAME) {
            return WireFormat.PROTO_ID_STRING;
        } else {
            return WireFormat.PROTO_ID_INT;
        }
    }

    public static <E extends Enum<E>> E readFrom(ProtoExInputStream inputStream, Class<E> clazz) {
        if (RuntimeEnv.ENUMS_BY_NAME) {
            String name = inputStream.readString();
            if (name != null)
                return Enum.valueOf(clazz, name);
        } else {
            int id = inputStream.readInt();
            boolean protoEnum = ProtoExEnum.class.isAssignableFrom(clazz);
            for (E e : clazz.getEnumConstants()) {
                if (protoEnum) {
                    if (((ProtoExEnum) e).getID() == id)
                        return e;
                } else {
                    if (e.ordinal() == id)
                        return e;
                }
            }
        }
        return null;
    }

}
