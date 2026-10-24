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

package com.tny.game.net.message.codec.protoex;

import com.google.common.base.MoreObjects;
import com.tny.game.protoex.annotations.*;

import java.util.Objects;

@ProtoEx(1001)
public class TestMsgObject {

    @ProtoExField(1)
    private int id;

    @ProtoExField(2)
    private String name;

    public TestMsgObject() {
    }

    public TestMsgObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestMsgObject)) {
            return false;
        }
        TestMsgObject testBody = (TestMsgObject) o;
        return getId() == testBody.getId() &&
               Objects.equals(getName(), testBody.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .toString();
    }

}
