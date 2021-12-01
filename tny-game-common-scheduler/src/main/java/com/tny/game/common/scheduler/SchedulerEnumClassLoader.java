package com.tny.game.common.scheduler;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;

import static com.tny.game.scanner.selector.EnumClassSelector.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class SchedulerEnumClassLoader {

	@ClassSelectorProvider
	static ClassSelector itemTypesSelector() {
		return createSelector(TaskReceiverType.class, TaskReceiverTypes::register);
	}

}
