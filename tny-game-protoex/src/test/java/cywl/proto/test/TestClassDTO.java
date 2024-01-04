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

package cywl.proto.test;

import com.tny.game.common.reflect.javassist.*;

public class TestClassDTO {

    private byte[] paramBytes = new byte[]{1, 2, 3, 4, 5};

    public TestClassDTO() {
    }

    public byte[] getParamBytes() {
        return paramBytes;
    }

    public void setParamBytes(byte[] paramBytes) {
        this.paramBytes = paramBytes;
    }

    public static void main(String[] args) {
        JavassistAccessors.getGClass(TestClassDTO.class);
    }

    //	", friend=" + ArrayUtils.toString(friendIDList) + ", equip=" + equip + ", goodsList=" + ArrayUtils.toString(goodsList) +

}
