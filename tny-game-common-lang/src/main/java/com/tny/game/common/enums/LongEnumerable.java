package com.tny.game.common.enums;

/**
 * Created by Kun Yang on 16/1/29.
 */

public interface LongEnumerable extends Enumerable<Long> {

	long id();

	default Long getId() {
		return id();
	}

}
