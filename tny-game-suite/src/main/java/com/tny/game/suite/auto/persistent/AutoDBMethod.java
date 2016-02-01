package com.tny.game.suite.auto.persistent;

import com.tny.game.suite.auto.AutoMethod;
import com.tny.game.suite.auto.persistent.annotation.AutoDB;
import com.tny.game.suite.auto.persistent.annotation.AutoDBParam;
import com.tny.game.suite.auto.persistent.annotation.AutoDBReturn;

import java.lang.reflect.Method;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class AutoDBMethod extends AutoMethod<AutoDB, AutoDBReturn, AutoDBParam> {

    protected AutoDBMethod(Method method) {
        super(method, AutoDB.class, AutoDBReturn.class, AutoDBParam.class);
    }

}
