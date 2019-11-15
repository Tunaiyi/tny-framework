package protoex;

import com.tny.game.protoex.annotations.*;
import org.apache.commons.lang3.builder.*;

import java.time.Instant;
import java.util.*;

/**
 * 模仿复杂对象
 *
 * @author Birdy
 */
@ProtoEx(1000)
public class MockComplexObject {

    @ProtoExField(1)
    private Integer id;

    @ProtoExField(2)
    private String firstName;

    @ProtoExField(3)
    private String lastName;

    @ProtoExField(4)
    private int money;

    @ProtoExField(5)
    private long instant;

    @ProtoExField(6)
    private MockEnumeration race;

    @ProtoExField(7)
    private String type;

    @ProtoExField(8)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    private LinkedList<MockSimpleObject> list;

    @ProtoExField(18)
    @ProtoExEntry(
            key = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT),
            value = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    private HashMap<Integer, MockSimpleObject> map;

    public MockComplexObject() {
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMoney() {
        return money;
    }

    public long getInstant() {
        return instant;
    }

    public MockEnumeration getRace() {
        return race;
    }

    public String[] toNames() {
        return new String[]{firstName, lastName};
    }

    public int[] toCurrencies() {
        return new int[]{money};
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MockComplexObject that = (MockComplexObject) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.id, that.id);
        equal.append(this.firstName, that.firstName);
        equal.append(this.lastName, that.lastName);
        equal.append(this.money, that.money);
        equal.append(this.instant, that.instant);
        equal.append(this.race, that.race);
        equal.append(this.type, that.type);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(id);
        hash.append(firstName);
        hash.append(lastName);
        hash.append(money);
        hash.append(instant);
        hash.append(race);
        hash.append(type);
        return hash.toHashCode();
    }

    public static MockComplexObject valueOf(Integer id, String firstName, String lastName, int money, Instant instant, MockEnumeration race) {
        MockComplexObject value = new MockComplexObject();
        value.id = id;
        value.firstName = firstName;
        value.lastName = lastName;
        value.money = money;
        value.instant = instant.toEpochMilli();
        value.race = race;
        value.list = new LinkedList<>();
        value.map = new HashMap<>();
        value.type = MockComplexObject.class.getSimpleName();
        for (int index = 0; index < money; index++) {
            MockSimpleObject object = MockSimpleObject.valueOf(index, lastName);
            value.list.add(object);
            value.map.put(index, object);
        }
        return value;
    }

}
