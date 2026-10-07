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
