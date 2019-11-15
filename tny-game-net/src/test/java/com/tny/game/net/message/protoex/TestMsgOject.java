package com.tny.game.net.message.protoex;

import com.google.common.base.MoreObjects;
import com.tny.game.protoex.annotations.*;

import java.util.Objects;

@ProtoEx(1001)
public class TestMsgOject {

    @ProtoExField(1)
    private int id;

    @ProtoExField(2)
    private String name;

    public TestMsgOject() {
    }

    public TestMsgOject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TestMsgOject))
            return false;
        TestMsgOject testBody = (TestMsgOject) o;
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
                          .add("id", id)
                          .add("name", name)
                          .toString();
    }
}
