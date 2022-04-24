package protoex.test;

import com.tny.game.protoex.annotations.*;

@ProtoEx(1010)
public class TestKey {

    @ProtoExField(1)
    public String key;

    public static TestKey key(String keyValue) {
        TestKey key = new TestKey();
        key.key = keyValue;
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestKey other = (TestKey)obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TestKey [key=" + key + "]";
    }

}
