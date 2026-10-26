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

package protoex.test;

import com.tny.game.protoex.annotations.*;

@ProtoEx(1001)
public class TestSubObject extends TestObject {

    @ProtoExField(100)
    public String subField = "";

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((subField == null) ? 0 : subField.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestSubObject other = (TestSubObject) obj;
        if (subField == null) {
            if (other.subField != null) {
                return false;
            }
        } else if (!subField.equals(other.subField)) {
            return false;
        }
        return true;
    }

}
