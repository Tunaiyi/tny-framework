package protoex;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 模仿简单对象
 * 
 * @author Birdy
 *
 */
@ProtoEx(1001)
public class MockSimpleObject {

	@ProtoExField(1)
	private long id;

	@ProtoExField(2)
	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		MockSimpleObject that = (MockSimpleObject) object;
		EqualsBuilder equal = new EqualsBuilder();
		equal.append(this.id, that.id);
		equal.append(this.name, that.name);
		return equal.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hash = new HashCodeBuilder();
		hash.append(id);
		hash.append(name);
		return hash.toHashCode();
	}

	public static MockSimpleObject valueOf(long id, String name) {
		MockSimpleObject object = new MockSimpleObject();
		object.id = id;
		object.name = name;
		return object;
	}

}
