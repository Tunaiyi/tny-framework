package com.tny.game.basics.persistent;

import com.tny.game.basics.auto.*;
import com.tny.game.basics.persistent.annotation.*;

import java.lang.reflect.Method;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class AutoManageMethod extends AutoMethod<Modifiable, ModifiableReturn, ModifiableParam, Immutable> {

	protected AutoManageMethod(Method method) {
		super(method, Modifiable.class, ModifiableReturn.class, ModifiableParam.class, Immutable.class);
	}

}
