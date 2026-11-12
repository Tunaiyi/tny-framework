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

package com.tny.game.zookeeper;

public interface NodeDataFormatter {

    /**
     * 对象转字节
     *
     * @param data 数据
     * @return 返回序列化字节数组
     */
    byte[] data2Bytes(Object data);

    /**
     * 字节转对象
     *
     * @param bytes 字节数组
     * @return 返回反序列化的对象
     */
    <D> D bytes2Data(byte[] bytes);

}
