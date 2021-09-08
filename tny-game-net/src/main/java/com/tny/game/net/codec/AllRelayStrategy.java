package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/7 3:46 下午
 */
@Unit
public class AllRelayStrategy implements MessageRelayStrategy {

	@Override
	public boolean isRelay(MessageHead head) {
		return true;
	}

}
