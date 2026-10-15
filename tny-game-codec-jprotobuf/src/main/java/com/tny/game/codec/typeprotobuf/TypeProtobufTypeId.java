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

package com.tny.game.codec.typeprotobuf;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 1:01 下午
 */
public interface TypeProtobufTypeId {

    int PB_BYTE = 1;
    int PB_SHORT = 2;
    int PB_INT = 3;
    int PB_LONG = 4;
    int PB_FLOAT = 5;
    int PB_DOUBLE = 6;
    int PB_BOOLEAN = 7;
    int PB_STRING = 8;

    int PB_BYTE_LIST = 11;
    int PB_SHORT_LIST = 12;
    int PB_INT_LIST = 13;
    int PB_LONG_LIST = 14;
    int PB_FLOAT_LIST = 15;
    int PB_DOUBLE_LIST = 16;
    int PB_BOOLEAN_LIST = 17;
    int PB_STRING_LIST = 18;

}
