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

package com.tny.game.protoex.annotations;

/**
 * ProtoEx 类型ID编码形式
 *
 * @author KGTny
 */
public enum TypeEncode {

    /**
     * 默认, 安装各个类型的默认形式进行编码
     */
    DEFAULT {
        @Override
        public boolean isExplicit(boolean defaultValue) {
            return defaultValue;
        }
    },

    /**
     * 显式编码ProtoExID
     * 解码以ProtoExID类型解码
     */
    EXPLICIT {
        @Override
        public boolean isExplicit(boolean defaultValue) {
            return true;
        }
    },

    /**
     * 隐式编码ProtoExID (不发送)
     * 解码以字段声明的Class进行解码
     * 如传输的数据为字段声明的Class的子类时,解码会丢失子类扩张的数据
     */
    IMPLICIT {
        @Override
        public boolean isExplicit(boolean defaultValue) {
            return false;
        }
    };

    /**
     * 是否显式发送
     *
     * @param defaultValue
     * @return
     */
    public abstract boolean isExplicit(boolean defaultValue);

    public static TypeEncode get(boolean explicit) {
        return explicit ? EXPLICIT : IMPLICIT;
    }

}
