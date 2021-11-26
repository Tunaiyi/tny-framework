package com.tny.game.common.enums;

/**
 * Created by Kun Yang on 16/1/29.
 */

public interface ByteEnumerable<ID> extends Enumerable<Byte> {

	byte id();

	@Override
	default Byte getId() {
		return id();
	}

}
