package cywl.proto.test;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;

@ProtoEx(101)
public class Goods {

    //	@Tag(1)
    @ProtoExField(1)
    private String name;

    //	@Tag(2)
    @ProtoExField(2)
    private String desc;

    public Goods() {
    }

    public Goods(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Goods [name=" + name + ", desc=" + desc + "]";
    }

}
