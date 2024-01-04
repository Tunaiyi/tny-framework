/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
