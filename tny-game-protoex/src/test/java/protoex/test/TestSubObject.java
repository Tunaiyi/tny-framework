package protoex.test;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;

@ProtoEx(1001)
public class TestSubObject extends TestObject {

    @ProtoExField(20)
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
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestSubObject other = (TestSubObject) obj;
        if (subField == null) {
            if (other.subField != null)
                return false;
        } else if (!subField.equals(other.subField))
            return false;
        return true;
    }

}
